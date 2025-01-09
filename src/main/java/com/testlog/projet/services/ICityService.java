package com.testlog.projet.services;

import java.time.LocalDateTime;
import java.util.List;

public interface ICityService<T> {
    List<T> getForCity(String city, LocalDateTime date);
}
