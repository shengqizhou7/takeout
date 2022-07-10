package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sailfinn.reggie.common.BaseContext;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.entity.ShoppingCart;
import com.sailfinn.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * add to shopping cart
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("shopping cart: {}", shoppingCart);

        //set user id, so we know whom this cart belongs to
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);


        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if(dishId != null){
            //dish
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        }else{
            //combo
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //check if dish/combo is already in cart
        //SQL: select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            //if already in cart, count + 1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //if not in cart, add to cart with default count (1)
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }


    /**
     * delete item in shopping cart
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("shopping cart: {}", shoppingCart);

        //set user id, so we know whom this cart belongs to
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if(dishId != null){
            //dish
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        }else{
            //combo
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //check if dish/combo is already in cart
        //SQL: select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        int number = cartServiceOne.getNumber();

        if(number > 1){
            //if count > 1, count--
            cartServiceOne.setNumber(--number);
            shoppingCartService.updateById(cartServiceOne);
        }else if(number == 1){
            //if count = 0, delete item
            cartServiceOne.setNumber(0);
            shoppingCartService.removeById(cartServiceOne);
        }
//        if(cartServiceOne != null){
//            //if already in cart, count + 1
//            Integer number = cartServiceOne.getNumber();
//            cartServiceOne.setNumber(number + 1);
//            shoppingCartService.updateById(cartServiceOne);
//        }else{
//            //if not in cart, add to cart with default count (1)
//            shoppingCart.setNumber(1);
//            shoppingCart.setCreateTime(LocalDateTime.now());
//            shoppingCartService.save(shoppingCart);
//            cartServiceOne = shoppingCart;
//        }

        return R.success(cartServiceOne);
    }

    /**
     * view shopping cart
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("view cart..");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }


    /**
     * clean shopping cart
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL: delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("Cart cleared!");
    }
}
