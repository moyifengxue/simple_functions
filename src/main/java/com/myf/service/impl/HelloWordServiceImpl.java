package com.myf.service.impl;

import com.myf.entity.HelloWord;
import com.myf.mapper.HelloWordMapper;
import com.myf.service.IHelloWordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author myf
 * @since 2022-07-27
 */
@Service
public class HelloWordServiceImpl extends ServiceImpl<HelloWordMapper, HelloWord> implements IHelloWordService {

}
