package com.mohamed.user_service.service.handler;

import com.mohamed.common.Ticker;
import com.mohamed.user.StockTradeRequest;
import com.mohamed.user.StockTradeResponse;
import com.mohamed.user_service.entity.PortfolioItem;
import com.mohamed.user_service.entity.UserEntity;
import com.mohamed.user_service.exceptions.InsufficientBalanceException;
import com.mohamed.user_service.exceptions.UnKnownUserException;
import com.mohamed.user_service.exceptions.UnknownTickerException;
import com.mohamed.user_service.repository.PortfolioItemRepository;
import com.mohamed.user_service.repository.UserRepository;
import com.mohamed.user_service.utils.EntityMessageMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockTradeRequestHandler {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public StockTradeRequestHandler(UserRepository userRepository,
                                    PortfolioItemRepository portfolioItemRepository) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional
    public StockTradeResponse buyStock(StockTradeRequest request) {
        // Validate request.
        this.validateTicker(request.getTicker());
        var user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnKnownUserException(request.getUserId()));
        var totalPrice = request.getQuantity() * request.getPrice();
        this.validateUserBalance(user, totalPrice);

        user.setBalance(user.getBalance() - totalPrice);
        this.portfolioItemRepository.findByUserIdAndTicker(user.getId(), request.getTicker())
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + request.getQuantity()),
                        () -> this.portfolioItemRepository.save(EntityMessageMapper.toPortfolioItem(request)));

        return EntityMessageMapper.toStockTradeResponse(request, user.getBalance());
    }

    @Transactional
    public StockTradeResponse sellStock(StockTradeRequest request) {
        // Validate request.
        this.validateTicker(request.getTicker());
        var user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnKnownUserException(request.getUserId()));
        var portfolioItem = this.portfolioItemRepository.findByUserIdAndTicker(user.getId(), request.getTicker())
                .filter(pi -> pi.getQuantity() >= request.getQuantity())
                .orElseThrow(InsufficientBalanceException::new);

        var totalPrice = request.getQuantity() * request.getPrice();

        user.setBalance(user.getBalance() + totalPrice);
        portfolioItem.setQuantity(portfolioItem.getQuantity() - request.getQuantity());

        return EntityMessageMapper.toStockTradeResponse(request, user.getBalance());
    }

    private void validateTicker(Ticker ticker) {
        if (Ticker.UNKNOWN.equals(ticker)) {
            throw new UnknownTickerException();
        }
    }

    private void validateUserBalance(UserEntity user, Integer totalPrice) {
        if (user.getBalance() < totalPrice) {
            throw new InsufficientBalanceException();
        }
    }
}
