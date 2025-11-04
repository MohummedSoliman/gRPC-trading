package com.mohamed.user_service.service.handler;


import com.mohamed.user.UserInformation;
import com.mohamed.user.UserInformationRequest;
import com.mohamed.user_service.exceptions.UnKnownUserException;
import com.mohamed.user_service.repository.PortfolioItemRepository;
import com.mohamed.user_service.repository.UserRepository;
import com.mohamed.user_service.utils.EntityMessageMapper;
import org.springframework.stereotype.Service;

@Service
public class UserInformationRequestHandler {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;


    public UserInformationRequestHandler(UserRepository userRepository,
                                         PortfolioItemRepository portfolioItemRepository) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public UserInformation getUserInformation(UserInformationRequest request) {
        var user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnKnownUserException(request.getUserId()));

        var portfolioItems = this.portfolioItemRepository.findAllByUserId(user.getId());

        return EntityMessageMapper.toUserInformation(user, portfolioItems);
    }
}
