package com.bescalonadev.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bescalonadev.springboot.app.models.entity.Categoria;

public interface ICategoriaDao extends PagingAndSortingRepository<Categoria, Long>{

}
