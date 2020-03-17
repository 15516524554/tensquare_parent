package com.tensquare.rabbitmq;

import java.util.Hashtable;

public class ThreadLocalTest {
  public static void main(String[] args) {
    ThreadLocal<Integer> tl = new ThreadLocal<>();
    tl.set(1);
    Hashtable<Object, Object> o1 = new Hashtable<>();
    o1.put(1, 1);
  }
}
