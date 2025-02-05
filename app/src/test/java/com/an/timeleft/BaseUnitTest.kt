package com.an.timeleft

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest(testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) {
    @Rule
    @JvmField
    val coroutinesTestRule = CoroutineTestRule(testDispatcher)
}