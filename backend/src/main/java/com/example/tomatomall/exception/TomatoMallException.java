package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
  public TomatoMallException(String message){
    super(message);
  }
  public static TomatoMallException usernameAlreadyExists(){
    return new TomatoMallException("用户名已经存在!");
  }

  public static TomatoMallException notLogin(){
    return new TomatoMallException("未登录!");
  }

  public static TomatoMallException phoneOrPasswordError(){
    return new TomatoMallException("手机号或密码错误!");
  }

  public static Exception storeNotFound() {
    return new TomatoMallException("商店未找到");
  }

  public static Exception storeAlreadyExists() {
    return new TomatoMallException("商店已存在");
  }
}
