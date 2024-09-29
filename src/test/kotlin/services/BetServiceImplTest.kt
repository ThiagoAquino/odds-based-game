package services

import dto.enum.ResultEnum
import dto.enum.TransactionTypeEnum
import odds.domain.Bet
import odds.domain.Player
import odds.domain.Transaction
import odds.dto.CreateTransactionDTO
import odds.exceptions.InsufficientBalanceException
import odds.exceptions.SafeBetException
import odds.repositories.BetRepository
import odds.services.BetServiceImpl
import odds.services.PlayerService
import odds.services.TransactionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


@ExtendWith(MockitoExtension::class)
class BetServiceImplTest {

    @Mock
    private lateinit var betRepository: BetRepository

    @Mock
    private lateinit var playerService: PlayerService

    @Mock
    private lateinit var transactionService: TransactionService

    @InjectMocks
    private lateinit var betServiceImpl: BetServiceImpl


    private lateinit var player: Player
    private lateinit var bet: Bet


    @BeforeEach
    fun setUp() {
        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)
        this.bet = Bet(
            id = 1,
            playerId = 1,
            betAmount = 10.0,
            betNumber = 5,
            generatedNumber = 3,
            result = ResultEnum.WIN,
            winnings = 50.0
        )
    }


    @Test
    fun `get players bet should return bets for a player`() {
        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))
        whenever(this.betRepository.findAllByPlayerId(this.player.id)).thenReturn(Flux.just(this.bet))

        val result = this.betServiceImpl.getPlayerBets(this.player.username)

        StepVerifier.create(result)
            .expectNext(this.bet)
            .verifyComplete()
    }


    @Test
    fun `placeBet should place a bet successfully`() {
        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))
        whenever(this.playerService.updatePlayerAmount(this.player)).thenReturn(Mono.just(this.player))
        whenever(this.betRepository.save(any<Bet>())).thenReturn(Mono.just(this.bet))
        whenever(this.transactionService.registerTransaction(any<CreateTransactionDTO>())).thenReturn(
            Mono.just(
                Transaction(
                    1,
                    this.player.id,
                    this.bet.betAmount,
                    TransactionTypeEnum.BET
                )
            )
        )


        val result = this.betServiceImpl.placeBet(this.player.username, this.bet.betAmount, this.bet.betNumber)

        StepVerifier.create(result)
            .expectNextMatches { it.betAmount == this.bet.betAmount && it.playerId == this.player.id }
            .verifyComplete()

        verify(this.transactionService).registerTransaction(any<CreateTransactionDTO>())
    }


    @Test
    fun `placeBet should throw InsufficientBalanceException when balance is insufficient`() {
        val playerWithLowBalance = this.player.copy(balance = 50.0)

        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(playerWithLowBalance))
        val result = this.betServiceImpl.placeBet(this.player.username, 150.0, this.bet.betNumber)

        StepVerifier.create(result)
            .expectError(InsufficientBalanceException::class.java)
            .verify()

        verify(this.transactionService, never()).registerTransaction(any())
    }

    @Test
    fun `placeBet should throw SafeBetException when betting more than 50 percent of the balance`() {

        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))
        val result = this.betServiceImpl.placeBet(this.player.username, 501.0, this.bet.betNumber)

        StepVerifier.create(result)
            .expectError(SafeBetException::class.java)
            .verify()

        verify(this.transactionService, never()).registerTransaction(any())
    }

}