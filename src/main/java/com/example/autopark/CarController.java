package com.example.autopark;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private static final int HISTOGRAM_DAYS = 30;

    private final CarRepository repository;

    public CarController(CarRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Car> getAll(@RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "asc") String order) {
        Sort.Direction direction = order.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        // сортировка по двум полям: год выпуска + марка
        Sort sort = Sort.by(new Sort.Order(direction, "releaseYear"), Sort.Order.asc("brand"));

        if (search.isBlank()) {
            return repository.findAll(sort);
        }
        return repository.search(search.trim(), sort);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Car car) {
        String error = validate(car);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        car.setId(null);
        return ResponseEntity.ok(repository.save(car));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Car car) {
        Car old = repository.findById(id).orElse(null);
        if (old == null) {
            return ResponseEntity.notFound().build();
        }
        String error = validate(car);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        old.setBrand(car.getBrand());
        old.setReleaseYear(car.getReleaseYear());
        old.setRegistrationDate(car.getRegistrationDate());
        old.setOwnerName(car.getOwnerName());
        return ResponseEntity.ok(repository.save(old));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/histogram")
    public List<Map<String, Object>> histogram() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(HISTOGRAM_DAYS - 1);

        Map<LocalDate, Long> counts = new HashMap<>();
        for (Object[] row : repository.countByDay(from)) {
            counts.put((LocalDate) row[0], (Long) row[1]);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < HISTOGRAM_DAYS; i++) {
            LocalDate day = from.plusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", day.toString());
            item.put("count", counts.getOrDefault(day, 0L));
            result.add(item);
        }
        return result;
    }

    private String validate(Car car) {
        if (car.getBrand() == null || !car.getBrand().trim().matches("[А-Яа-яЁё0-9\\- ]+")) {
            return "Марка должна быть написана кириллицей";
        }
        int maxYear = LocalDate.now().getYear() + 1;
        if (car.getReleaseYear() < 1900 || car.getReleaseYear() > maxYear) {
            return "Год выпуска должен быть от 1900 до " + maxYear;
        }
        if (car.getRegistrationDate() == null) {
            return "Укажите дату постановки на учёт";
        }
        if (car.getOwnerName() == null || car.getOwnerName().isBlank()) {
            return "Укажите ФИО владельца";
        }
        car.setBrand(car.getBrand().trim());
        car.setOwnerName(car.getOwnerName().trim());
        return null;
    }
}
