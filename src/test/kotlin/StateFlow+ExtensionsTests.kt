import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.flattenLatestState
import aetherealtech.kotlinflowsextensions.mapState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.seconds

class StateFlowExtensionsTests {
    @Test
    fun testMap() = runTest {
        val source = MutableStateFlow(0)

        val result = source.mapState { intValue -> "$intValue" }

        val collected = mutableListOf<String>()

        val channel = Channel<Unit>(0)

        val job = launch {
            result
                .collect { value ->
                    collected.add(value)
                    channel.send(Unit)
                }
        }

        channel.receive()

        for (i in 0 ..< 10) {
            assertEquals("${source.value}", result.value)
            source.value += 1
            channel.receive()
        }

        job.cancel()
        job.join()

        assertEquals((0..<11).map(Int::toString).toList(), collected)
    }

    @Test
    fun testFlattenLatest() = runTest(timeout = INFINITE) {
        val source = MutableStateFlow(MutableStateFlow(0))

        val result = source.flattenLatestState()

        val collected = mutableListOf<Int>()

        val channel = Channel<Unit>(0)

        val job = launch {
            result
                .collect { value ->
                    collected.add(value)
                    channel.send(Unit)
                }
        }

        channel.receive()

        for (i in 0 ..< 10) {
            for (j in 0 ..< 10) {
                assertEquals(source.value.value, result.value)
                source.value.value += 1
                channel.receive()
            }

            source.value = MutableStateFlow((i + 1) * 11)
            channel.receive()
        }

        job.cancel()
        job.join()

        assertEquals((0..<111).toList(), collected)
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