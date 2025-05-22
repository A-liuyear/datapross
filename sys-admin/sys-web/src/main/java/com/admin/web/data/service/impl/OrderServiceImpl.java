package com.admin.web.data.service.impl;

import com.admin.web.data.entity.Order;
import com.admin.web.data.mapper.OrderMapper;
import com.admin.web.data.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


}
