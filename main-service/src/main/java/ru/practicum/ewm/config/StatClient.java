package ru.practicum.ewm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.korshunov.statsclient.StatsClient;

@Configuration
@ComponentScan(basePackages = {"ru.korshunov.statsclient"})
public class StatClient {
}
