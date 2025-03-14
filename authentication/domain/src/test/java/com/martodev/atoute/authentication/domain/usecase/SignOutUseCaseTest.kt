package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@ExperimentalCoroutinesApi
class SignOutUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var signOutUseCase: SignOutUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        signOutUseCase = SignOutUseCase(authRepository)
    }
    
    @Test
    fun `invoke calls signOut on repository`() = runTest {
        // When
        signOutUseCase()
        
        // Then
        verify(authRepository).signOut()
        verifyNoMoreInteractions(authRepository)
    }
} 