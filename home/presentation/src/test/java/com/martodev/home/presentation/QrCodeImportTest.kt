package com.martodev.home.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.domain.usecase.CheckPartyLimitUseCase
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.home.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoStatusUseCase
import com.martodev.atoute.home.presentation.HomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class QrCodeImportTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @MockK
    lateinit var getPartiesUseCase: GetPartiesUseCase

    @MockK
    lateinit var getPriorityTodosUseCase: GetPriorityTodosUseCase

    @MockK
    lateinit var savePartyUseCase: SavePartyUseCase

    @MockK
    lateinit var updateTodoStatusUseCase: UpdateTodoStatusUseCase

    @MockK
    lateinit var authService: AuthService

    @MockK
    lateinit var checkPartyLimitUseCase: CheckPartyLimitUseCase

    @MockK
    lateinit var saveTodoUseCase: SaveTodoUseCase

    @MockK
    lateinit var saveToBuyUseCase: SaveToBuyUseCase

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Configuration des mocks
        coEvery { savePartyUseCase(any()) } returns "new-party-id"
        coEvery { saveTodoUseCase(any()) } just Runs
        coEvery { saveToBuyUseCase(any()) } just Runs
        coEvery { authService.registerCurrentUserAsOwner(any()) } just Runs
        every { getPartiesUseCase() } returns flowOf(emptyList())
        every { getPriorityTodosUseCase() } returns flowOf(emptyList())

        // Initialisation du ViewModel
        viewModel = HomeViewModel(
            getPartiesUseCase = getPartiesUseCase,
            getPriorityTodosUseCase = getPriorityTodosUseCase,
            savePartyUseCase = savePartyUseCase,
            updateTodoStatusUseCase = updateTodoStatusUseCase,
            authService = authService,
            checkPartyLimitUseCase = checkPartyLimitUseCase,
            saveTodoUseCase = saveTodoUseCase,
            saveToBuyUseCase = saveToBuyUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processScannedQrCode should import todos and tobuys`() = runTest {
        // Arrange
        val now = LocalDateTime.now()
        val qrCodeData = "event-123|Party Test|$now|123 Rue Test|0xFFE91E63|Description test|User1,User2|" +
                "todo1::Tâche 1::false::::true,todo2::Tâche 2::false::::false|" +
                "tobuy1::Achat 1::2::15.5::false::::false,tobuy2::Achat 2::1::10.0::false::::true"

        // Act
        viewModel.processScannedQrCode(qrCodeData)
        
        // Avancer le temps pour que toutes les coroutines soient terminées
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { savePartyUseCase(any()) }
        coVerify(exactly = 2) { saveTodoUseCase(any()) }
        coVerify(exactly = 2) { saveToBuyUseCase(any()) }
        coVerify(exactly = 1) { authService.registerCurrentUserAsOwner("new-party-id") }
        
        // Vérifier les arguments des appels
        coVerify {
            saveTodoUseCase(match { todo ->
                todo.partyId == "new-party-id" && 
                (todo.title == "Tâche 1" || todo.title == "Tâche 2")
            })
        }
        
        coVerify {
            saveToBuyUseCase(match { toBuy ->
                toBuy.partyId == "new-party-id" && 
                (toBuy.title == "Achat 1" || toBuy.title == "Achat 2")
            })
        }
    }

    @Test
    fun `processScannedQrCode should handle empty todos and tobuys`() = runTest {
        // Arrange
        val now = LocalDateTime.now()
        val qrCodeData = "event-123|Party Test|$now|123 Rue Test|0xFFE91E63|Description test|User1,User2||"

        // Act
        viewModel.processScannedQrCode(qrCodeData)
        
        // Avancer le temps pour que toutes les coroutines soient terminées
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { savePartyUseCase(any()) }
        coVerify(exactly = 0) { saveTodoUseCase(any()) }
        coVerify(exactly = 0) { saveToBuyUseCase(any()) }
        coVerify(exactly = 1) { authService.registerCurrentUserAsOwner("new-party-id") }
    }

    @Test
    fun `processScannedQrCode should handle malformed todo and tobuy data`() = runTest {
        // Arrange
        val now = LocalDateTime.now()
        val qrCodeData = "event-123|Party Test|$now|123 Rue Test|0xFFE91E63|Description test|User1,User2|" +
                "invalid-todo-format|invalid-tobuy-format"

        // Act
        viewModel.processScannedQrCode(qrCodeData)
        
        // Avancer le temps pour que toutes les coroutines soient terminées
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { savePartyUseCase(any()) }
        coVerify(exactly = 0) { saveTodoUseCase(any()) }
        coVerify(exactly = 0) { saveToBuyUseCase(any()) }
        coVerify(exactly = 1) { authService.registerCurrentUserAsOwner("new-party-id") }
    }
} 