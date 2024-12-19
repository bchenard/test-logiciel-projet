package com.testlog.projet.services;

import java.util.List;

public interface ICityService<T> {
    List<T> getForCity(String city);
}
