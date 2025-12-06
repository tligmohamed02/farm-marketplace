package net.mohamed.devwebproject;

import net.mohamed.devwebproject.entity.DeliveryPerson;
import net.mohamed.devwebproject.repository.CustomerRepository;
import net.mohamed.devwebproject.repository.DeliveryPersonRepository;
import net.mohamed.devwebproject.repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class DevWebProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevWebProjectApplication.class, args);
    }


    //@Bean
    public CommandLineRunner demo(DeliveryPersonRepository deliveryPersonRepository, DeliveryRepository deliveryRepository, CustomerRepository customerRepository) {
        return args -> {
            Stream.of("Ahmed", "Nour", "Fedi").forEach(name -> {
                DeliveryPerson d1 = new DeliveryPerson();
                d1.setName(name);
                d1.setEmail(name + "@gmail.com");
                d1.setPhone("22021931");
                d1.setVehicleType("Moto");
                deliveryPersonRepository.save(d1);
            });
        };
    }
}
