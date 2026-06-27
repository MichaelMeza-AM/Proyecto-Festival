package com.festival.artista_service;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.festival.artista_service.model.Artista;
import com.festival.artista_service.repository.ArtistaRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final ArtistaRepository artistaRepository;

    public DataLoader(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();

        // Generar 5 artistas aleatorios usando Faker
        for (int i = 0; i < 5; i++) {
            Artista artista = new Artista();
            
            artista.setNombre(faker.rockBand().name());
            
            String biografiaFalsa = faker.lorem().sentence(25);
            if (biografiaFalsa.length() > 600) {
                biografiaFalsa = biografiaFalsa.substring(0, 599);
            }
            artista.setBiografia(biografiaFalsa);
            
            // Limitamos las opciones de género 
            artista.setGeneroMusical(faker.options().option("Rock", "Pop", "Electrónica", "Urbano", "Metal"));

            artistaRepository.save(artista);
        }
    }
}