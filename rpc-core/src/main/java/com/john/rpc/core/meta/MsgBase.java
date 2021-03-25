package com.john.rpc.core.meta;

import lombok.Data;

@Data
public class MsgBase {
    private String body;

    private int  Type;
    private int  Sequence;
}
