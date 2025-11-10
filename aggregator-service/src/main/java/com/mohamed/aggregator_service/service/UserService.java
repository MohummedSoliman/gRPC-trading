package com.mohamed.aggregator_service.service;

import com.mohamed.user.UserInformation;
import com.mohamed.user.UserInformationRequest;
import com.mohamed.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userClient;

    public UserInformation getUserInformation(int userId) {
        var request = UserInformationRequest.newBuilder()
                .setUserId(userId)
                .build();

        return this.userClient.getUserInformation(request);
    }
}
