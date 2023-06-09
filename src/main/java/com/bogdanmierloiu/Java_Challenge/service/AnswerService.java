package com.bogdanmierloiu.Java_Challenge.service;

import com.bogdanmierloiu.Java_Challenge.dto.answer.AnswerRequest;
import com.bogdanmierloiu.Java_Challenge.dto.answer.AnswerResponse;
import com.bogdanmierloiu.Java_Challenge.entity.Answer;
import com.bogdanmierloiu.Java_Challenge.entity.Player;
import com.bogdanmierloiu.Java_Challenge.entity.Question;
import com.bogdanmierloiu.Java_Challenge.mapper.AnswerMapper;
import com.bogdanmierloiu.Java_Challenge.repository.AnswerRepository;
import com.bogdanmierloiu.Java_Challenge.repository.PlayerRepository;
import com.bogdanmierloiu.Java_Challenge.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService implements CrudOperation<AnswerRequest, AnswerResponse> {
    private final AnswerRepository answerRepository;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;
    private final WalletHistoryService walletHistoryService;
    private final AnswerMapper answerMapper;
    private final NftService nftService;

    private final ReputationService reputationService;


    @Override
    public AnswerResponse add(AnswerRequest request) throws DataIntegrityViolationException {
        if (request.getText().length() > 2000) {
            throw new DataIntegrityViolationException("Answer is too long! Max : 2000 characters");
        }
        Answer answerToSave = answerMapper.map(request);
        answerToSave.setIsValid(false);
        answerToSave.setPlayer(playerRepository.findById(request.getPlayerId()).orElseThrow());
        answerToSave.setQuestion(questionRepository.findById(request.getQuestionId()).orElseThrow());
        return answerMapper.map(answerRepository.save(answerToSave));
    }

    public List<AnswerResponse> findByQuestion(Long id) {
        return answerMapper.map(answerRepository.findByQuestionId(id));
    }

    public void validateAnswer(Long answerId, Long playerId, Long questionId, Long playerWhoTryToValidateId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();
        Player playerWhoAddAnswer = playerRepository.findById(playerId).orElseThrow();
        Question question = questionRepository.findById(questionId).orElseThrow();
        Player playerWhoPutTheQuestion = question.getPlayer();
        if (validateUser(playerWhoPutTheQuestion, playerWhoTryToValidateId)) {
            answer.setIsValid(true);
            question.setIsResolved(true);
            playerWhoAddAnswer.getWallet().setNrOfTokens(playerWhoAddAnswer.getWallet().getNrOfTokens() + question.getRewardTokens());
            playerWhoPutTheQuestion.getWallet().setNrOfTokens(playerWhoPutTheQuestion.getWallet().getNrOfTokens() - question.getRewardTokens());
            answerRepository.save(answer);

            walletHistoryService.createRewardEvent(playerWhoPutTheQuestion, playerWhoAddAnswer, question);
            addNftReputationForPlayer(playerWhoAddAnswer);
        }
    }


    public boolean validateUser(Player player, Long id) {
        return Objects.equals(player.getId(), id);
    }

    public Long validAnswersForPlayer(Player player) {
        return (long) answerRepository.findAllByPlayerIdAndIsValid(player.getId(), true).size();
    }

    public void addNftReputationForPlayer(Player playerWhoAddAnswer) {
        if (validAnswersForPlayer(playerWhoAddAnswer) == 3) {
            playerWhoAddAnswer.getWallet().getNfts().add(nftService.createAdventurerNFT());
            playerWhoAddAnswer.setReputation(reputationService.createAdventurerReputation());
        }
        if (validAnswersForPlayer(playerWhoAddAnswer) == 5) {
            playerWhoAddAnswer.getWallet().getNfts().add(nftService.createCosmonautNFT());
            playerWhoAddAnswer.setReputation(reputationService.createCosmonautReputation());
        }
    }

    public List<AnswerResponse> findAllByPlayerId(Long playerId) {
        return answerMapper.map(answerRepository.findAllByPlayerId(playerId));
    }

    @Override
    public List<AnswerResponse> getAll() {
        return answerMapper.map(answerRepository.findAll());
    }

    @Override
    public AnswerResponse findById(Long id) {
        return answerMapper.map(answerRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Answer not found!")
        ));
    }

    @Override
    public AnswerResponse update(AnswerRequest request) throws DataIntegrityViolationException {
        if (request.getText().length() > 2000) {
            throw new DataIntegrityViolationException("Answer is too long! Max : 2000 characters");
        }
        Answer answerToUpdate = answerRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Answer not found"));
        answerToUpdate.setText(request.getText() != null ? request.getText() : answerToUpdate.getText());
        return answerMapper.map(answerRepository.save(answerToUpdate));
    }

    @Override
    public void delete(Long id) {
        Answer answerToDelete = answerRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Answer not found"));
        answerRepository.delete(answerToDelete);
    }
}
