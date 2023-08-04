package com.test.client;

import com.test.entity.RPCRequest;
import com.test.entity.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);
}
