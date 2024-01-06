import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StateFlowExtensionsTests {
    @Test
    fun testMap() = runTest {
        val source = MutableStateFlow(0)

        val result = source.mapState { intValue -> "$intValue" }

        for (i in 0 ..< 10) {
            assertEquals(result.value, "${source.value}")
            source.value += 1
        }
    }

    @Test
    fun testCombine() = runTest {
        val source1 = MutableStateFlow(0)
        val source2 = MutableStateFlow("0")

        val result = StateFlows.combine(source1, source2)

        for (i in 0 ..< 10) {
            assertEquals(result.value, Pair(source1.value, source2.value))
            source1.value += 1
        }

        for (i in 0 ..< 10) {
            assertEquals(result.value, Pair(source1.value, source2.value))
            source2.value += 1
        }
    }
}