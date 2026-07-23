package com.eiou.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eiou.mall.product.entity.MallProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<MallProduct> {
}
