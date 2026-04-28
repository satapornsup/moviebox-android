package com.mastercoding.moviebox.presentation.home

import com.mastercoding.moviebox.data.repo.MovieRepository
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.domain.model.MoviePage
import com.mastercoding.moviebox.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private lateinit var repo: MovieRepository

    private fun movie(id: Int, title: String = "M$id") = Movie(
        id = id,
        title = title,
        overview = "",
        posterPath = null,
        backdropPath = null,
        releaseDate = null,
        voteAverage = 0.0,
    )

    private fun page(
        ids: IntRange,
        page: Int,
        totalPages: Int,
    ) = MoviePage(
        movies = ids.map { movie(it) },
        page = page,
        totalPages = totalPages,
    )

    @Before
    fun setup() {
        repo = mockk()
        // Default: empty popular page so init() doesn't crash unstubbed.
        coEvery { repo.popular(any(), any()) } returns page(IntRange.EMPTY, 1, 1)
    }

    // ─── init ──────────────────────────────────────────────────────────

    @Test
    fun `init triggers loadPopular and populates state`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, page = 1, totalPages = 5)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(3, s.movies.size)
        assertEquals(listOf(1, 2, 3), s.movies.map { it.id })
        assertEquals(1, s.page)
        assertFalse(s.loading)
        assertFalse(s.endReached)
        coVerify(exactly = 1) { repo.popular(page = 1, limit = 10) }
    }

    @Test
    fun `init sets endReached when first page is the only page`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, page = 1, totalPages = 1)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        assertTrue(vm.state.value.endReached)
    }

    @Test
    fun `init failure puts error in state`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } throws RuntimeException("boom")

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        val s = vm.state.value
        assertFalse(s.loading)
        assertEquals("boom", s.error)
        assertTrue(s.movies.isEmpty())
    }

    // ─── onQueryChange ─────────────────────────────────────────────────

    @Test
    fun `query change debounce - only final value triggers search`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.search("batman") } returns listOf(movie(99, "Batman"))

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        // Type 4 chars in quick succession (each well under 300ms debounce)
        vm.onEvent(HomeUiEvent.QueryChange("b"))
        advanceTimeBy(50)
        vm.onEvent(HomeUiEvent.QueryChange("ba"))
        advanceTimeBy(50)
        vm.onEvent(HomeUiEvent.QueryChange("bat"))
        advanceTimeBy(50)
        vm.onEvent(HomeUiEvent.QueryChange("batman"))
        advanceUntilIdle()

        coVerify(exactly = 0) { repo.search("b") }
        coVerify(exactly = 0) { repo.search("ba") }
        coVerify(exactly = 0) { repo.search("bat") }
        coVerify(exactly = 1) { repo.search("batman") }
        assertEquals(listOf(99), vm.state.value.movies.map { it.id })
    }

    @Test
    fun `clearing query reloads popular`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.search("x") } returns listOf(movie(99))

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.QueryChange("x"))
        advanceUntilIdle()
        assertEquals(listOf(99), vm.state.value.movies.map { it.id })

        vm.onEvent(HomeUiEvent.QueryChange(""))
        advanceUntilIdle()

        // Back to popular page 1
        assertEquals(listOf(1, 2, 3), vm.state.value.movies.map { it.id })
        // popular called twice: init + clear
        coVerify(exactly = 2) { repo.popular(page = 1, limit = 10) }
    }

    @Test
    fun `search freezes pagination - endReached forced true`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.search("y") } returns listOf(movie(50))

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.QueryChange("y"))
        advanceUntilIdle()

        assertTrue("search must freeze pagination", vm.state.value.endReached)
    }

    // ─── loadMore ──────────────────────────────────────────────────────

    @Test
    fun `loadMore appends next page and bumps page counter`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.popular(page = 2, limit = 10) } returns page(4..6, 2, 5)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.LoadMore)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(listOf(1, 2, 3, 4, 5, 6), s.movies.map { it.id })
        assertEquals(2, s.page)
        assertFalse(s.endReached)
        assertFalse(s.isLoadingMore)
    }

    @Test
    fun `loadMore sets endReached when last page reached`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 2)
        coEvery { repo.popular(page = 2, limit = 10) } returns page(4..6, 2, 2)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.LoadMore)
        advanceUntilIdle()

        assertTrue(vm.state.value.endReached)
    }

    @Test
    fun `loadMore no-op when endReached`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 1)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()
        assertTrue(vm.state.value.endReached)

        vm.onEvent(HomeUiEvent.LoadMore)
        advanceUntilIdle()

        // Only init's popular(page=1) call — no page=2 attempted
        coVerify(exactly = 1) { repo.popular(any(), any()) }
    }

    @Test
    fun `loadMore no-op when query is non-blank`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.search("x") } returns listOf(movie(99))

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.QueryChange("x"))
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.LoadMore)
        advanceUntilIdle()

        // popular called only on init — loadMore must not paginate during search
        coVerify(exactly = 1) { repo.popular(any(), any()) }
    }

    @Test
    fun `loadMore failure surfaces error and clears isLoadingMore`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.popular(page = 2, limit = 10) } throws RuntimeException("net")

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.LoadMore)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals("net", s.error)
        assertFalse(s.isLoadingMore)
        // First page survives
        assertEquals(listOf(1, 2, 3), s.movies.map { it.id })
        // page counter NOT bumped on failure
        assertEquals(1, s.page)
    }

    // ─── retry ─────────────────────────────────────────────────────────

    @Test
    fun `retry from popular re-runs popular`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 2) { repo.popular(page = 1, limit = 10) }
    }

    @Test
    fun `retry from search re-runs search`() = runTest {
        coEvery { repo.popular(page = 1, limit = 10) } returns page(1..3, 1, 5)
        coEvery { repo.search("dune") } returns listOf(movie(42, "Dune"))

        val vm = HomeViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.QueryChange("dune"))
        advanceUntilIdle()

        vm.onEvent(HomeUiEvent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 2) { repo.search("dune") }
    }
}
