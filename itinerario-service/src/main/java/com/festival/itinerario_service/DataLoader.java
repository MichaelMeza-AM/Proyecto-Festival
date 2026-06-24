package com.festival.itinerario_service;

import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.repository.ItinerarioRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements CommandLineRunner {

    private final ItinerarioRepository repository;

    public DataLoader(ItinerarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        // Generar 5 itinerarios aleatorios directamente
        for (int i = 0; i < 5; i++) {
            Itinerario itinerario = new Itinerario();
            
            itinerario.setUsuarioId((long) random.nextInt(2) + 1);
            itinerario.setPresentacionId((long) random.nextInt(5) + 1);
            
            java.util.Date fakeDate = faker.date().past(30, TimeUnit.DAYS);
            LocalDateTime fechaAleatoria = fakeDate.toInstant()
                                                   .atZone(ZoneId.systemDefault())
                                                   .toLocalDateTime();
            itinerario.setFechaAgregado(fechaAleatoria);

            repository.save(itinerario);
        }
    }
}