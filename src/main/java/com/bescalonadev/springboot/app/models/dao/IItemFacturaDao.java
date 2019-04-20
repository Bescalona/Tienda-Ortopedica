package com.bescalonadev.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bescalonadev.springboot.app.models.entity.ItemFactura;

public interface IItemFacturaDao extends CrudRepository<ItemFactura, Long>{

}
