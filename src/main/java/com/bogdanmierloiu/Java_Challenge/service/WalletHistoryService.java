package com.bogdanmierloiu.Java_Challenge.service;

import com.bogdanmierloiu.Java_Challenge.dto.player.PlayerResponse;
import com.bogdanmierloiu.Java_Challenge.dto.wallet_history.WalletHistoryRequest;
import com.bogdanmierloiu.Java_Challenge.dto.wallet_history.WalletHistoryResponse;
import com.bogdanmierloiu.Java_Challenge.entity.Player;
import com.bogdanmierloiu.Java_Challenge.entity.Question;
import com.bogdanmierloiu.Java_Challenge.entity.Wallet;
import com.bogdanmierloiu.Java_Challenge.entity.WalletHistory;
import com.bogdanmierloiu.Java_Challenge.mapper.WalletHistoryMapper;
import com.bogdanmierloiu.Java_Challenge.repository.WalletHistoryRepository;
import com.bogdanmierloiu.Java_Challenge.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletHistoryService implements CrudOperation<WalletHistoryRequest, WalletHistoryResponse> {
    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    private final WalletHistoryMapper walletHistoryMapper;


    public void createBonusEvent(String event, Wallet wallet) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setDateTime(LocalDateTime.now());
        walletHistory.setEvent(event);
        walletHistory.setWallet(wallet);
        walletHistory.setValue(wallet.getNrOfTokens());
        walletHistoryRepository.save(walletHistory);
    }

    public void createRewardEvent(Player playerWhoPutQuestion, Player playerWhoAddAnswer, Question question) {
        WalletHistory senderHistory = new WalletHistory();
        senderHistory.setWallet(playerWhoPutQuestion.getWallet());
        senderHistory.setDateTime(LocalDateTime.now());
        senderHistory.setEvent("Sent to " + playerWhoAddAnswer.getName());
        senderHistory.setValue(-question.getRewardTokens());

        WalletHistory receiverHistory = new WalletHistory();
        receiverHistory.setWallet(playerWhoAddAnswer.getWallet());
        receiverHistory.setDateTime(LocalDateTime.now());
        receiverHistory.setEvent("Received from " + playerWhoPutQuestion.getName());
        receiverHistory.setValue(question.getRewardTokens());

        walletHistoryRepository.saveAll(Arrays.asList(senderHistory, receiverHistory));
    }


    public void createTokenTransferEvent(Wallet senderWallet, Wallet receiverWallet, Long nrOfTokens) {
        WalletHistory senderHistory = new WalletHistory();
        senderHistory.setDateTime(LocalDateTime.now());
        senderHistory.setEvent("Sent to " + receiverWallet.getAddress());
        senderHistory.setWallet(senderWallet);
        senderHistory.setValue(-nrOfTokens);

        WalletHistory receiverHistory = new WalletHistory();
        receiverHistory.setDateTime(LocalDateTime.now());
        receiverHistory.setEvent("Received from " + senderWallet.getAddress());
        receiverHistory.setWallet(receiverWallet);
        receiverHistory.setValue(nrOfTokens);

        walletHistoryRepository.saveAll(Arrays.asList(senderHistory, receiverHistory));
    }


    @Override
    public WalletHistoryResponse add(WalletHistoryRequest request) {
        WalletHistory walletHistory = walletHistoryMapper.map(request);
        walletHistory.setDateTime(LocalDateTime.now());
        walletHistory.setWallet(walletRepository.findById(request.getWalletId()).orElseThrow());
        return walletHistoryMapper.map(walletHistoryRepository.save(walletHistory));
    }

    public List<WalletHistoryResponse> findByWallet(Long id) {
        return walletHistoryMapper.map(walletHistoryRepository.findByWalletIdOrderByDateTimeDesc(id));
    }

    @Override
    public List<WalletHistoryResponse> getAll() {
        return walletHistoryMapper.map(walletHistoryRepository.findAll());
    }

    @Override
    public WalletHistoryResponse findById(Long id) {
        return walletHistoryMapper.map(walletHistoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("History not found")
        ));
    }

    @Override
    public WalletHistoryResponse update(WalletHistoryRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
