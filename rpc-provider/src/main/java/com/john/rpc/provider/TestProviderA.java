package com.john.rpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProviderA {

  private static final Logger logger = LoggerFactory.getLogger(TestProviderA.class);

  public String getMsg(String msg) {
    logger.info("out:",msg);
    return msg;
  }

}
