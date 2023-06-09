package com.bogdanmierloiu.Java_Challenge.controller.mvc;

import com.bogdanmierloiu.Java_Challenge.dto.player.PlayerResponse;
import com.bogdanmierloiu.Java_Challenge.exception.AccessDeniedException;
import com.bogdanmierloiu.Java_Challenge.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/admin")
@SessionAttributes("player")
@RequiredArgsConstructor
public class AdminController {
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final WalletService walletService;
    private final WalletHistoryService walletHistoryService;
    private final AnswerService answerService;
    private final HttpSession session;


    private void isAdmin() throws AccessDeniedException {
        PlayerResponse playerFromSession = (PlayerResponse) session.getAttribute("player");
        if (playerFromSession == null || !Objects.equals(playerFromSession.getName(), "admin")) {
            throw new AccessDeniedException("Access denied");
        }
    }

    @GetMapping("/admin-page")
    public String goToAdminPage() {
        try {
            isAdmin();
            return "admin-page";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/all-players")
    public String showAllPlayers(Model model) {
        try {
            isAdmin();
            model.addAttribute("players", playerService.findAllByOrderByTokens());
            return "admin-all-players";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/all-questions")
    public String showAllQuestions(Model model) {
        try {
            isAdmin();
            model.addAttribute("questions", questionService.getAll());
            return "admin-all-questions";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/all-wallets")
    public String showAllWallets(Model model) {
        try {
            isAdmin();
            model.addAttribute("wallets", walletService.getAll());
            return "admin-all-wallets";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/wallet-history/{walletId}")
    public String showWalletHistory(@PathVariable("walletId") String walletId, Model model) {
        try {
            isAdmin();
            model.addAttribute("player", playerService.findByWalletId(Long.parseLong(walletId)));
            model.addAttribute("walletHistory", walletHistoryService.findByWallet(Long.parseLong(walletId)));
            return "admin-wallet-history";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/questions-for-player/{playerId}")
    public String viewQuestionsForPlayer(@PathVariable("playerId") Long playerId, Model model) {
        model.addAttribute("questions", questionService.findAllByPlayer(playerId));
        model.addAttribute("playerForQuestions", playerService.findById(playerId));
        return "questions-for-player";
    }

    @GetMapping("/answers-for-player/{playerId}")
    public String viewAnswersForPlayer(@PathVariable("playerId") Long playerId, Model model) {
        model.addAttribute("playerForAnswers", playerService.findById(playerId));
        model.addAttribute("answers", answerService.findAllByPlayerId(playerId));
        return "answers-for-player";
    }

    @GetMapping("/block/{playerId}")
    public String blockPlayer(@PathVariable("playerId") Long playerId, Model model) {
        try {
            isAdmin();
            playerService.blockPlayer(playerId);
            model.addAttribute("players", playerService.findAllByOrderByTokens());
            return "admin-all-players";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/unblock/{playerId}")
    public String unblockPlayer(@PathVariable("playerId") Long playerId, Model model) {
        try {
            isAdmin();
            playerService.unblockPlayer(playerId);
            model.addAttribute("players", playerService.findAllByOrderByTokens());
            return "admin-all-players";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

    @GetMapping("/history")
    public String getHistory(Model model) {
        try {
            isAdmin();
            model.addAttribute("histories", walletHistoryService.getAll());
            return "admin-histories";
        } catch (AccessDeniedException ex) {
            return "access-denied";
        }
    }

}
