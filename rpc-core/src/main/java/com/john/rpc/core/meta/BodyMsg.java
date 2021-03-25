package com.john.rpc.core.meta;

import lombok.Data;

/**
 * rpc//com.test.abc:methodName?param1=123;param2=345
 */
@Data
public class BodyMsg {

  private String clazzName;

  private String methodName;

  private Object[] params;

  private Class[] paramTyeps;

}
