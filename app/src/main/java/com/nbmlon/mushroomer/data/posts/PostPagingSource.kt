package com.nbmlon.mushroomer.data.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import com.nbmlon.mushroomer.ui.commu.board.PostSortingOption
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException
import kotlin.math.max

private const val STARTING_KEY = 0


/**
 * @param backend           sevice
 * @param searchKeyword     searchKeyword
 * @param boardType         boardType ( 자유게시판, QnA, 사진 게시판 )
 * @param isHotBoard        Hot게시판 구별
 * @param isHistoryMyBoard  내가 쓴 글 보기
 */
class PostPagingSource(
    val backend: BoardService,
    val boardType: BoardType,
    val searchKeyword: String? = null,
    val sortingOption: PostSortingOption = PostSortingOption.SORTING_TIME,
    val isHotBoard : Boolean = false,
    val isHistoryMyBoard : Boolean = false,
    val isHistoryMyCommentBoard : Boolean = false
) : PagingSource<Int, Post >() {
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, Post> {

        try {
            var query : String = ""
            // Start refresh at page 1 if undefined.
            val startKey = params.key ?: STARTING_KEY
            val range = startKey.until(startKey + params.loadSize)

            /**
            if(isHotBoard)
                //핫게 처리문
            if(isHistoryMyBoard)
                //작성글 보기 처리
            if(isHistoryMyCommentBoard)
                //작성댓글보기 처리
            */
            val response = backend.getBoardPosts(query, boardType.name).await()

            //정렬 쿼리 정의
            when(sortingOption){
                PostSortingOption.SORTING_TIME ->{};
                PostSortingOption.SORTING_LIKE ->{};
            }

            return LoadResult.Page(
                data = response.posts.map {it.toPost()},

                prevKey = when (startKey) {
                    STARTING_KEY -> null
                    else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
                        // We're at the start, there's nothing more to load
                        STARTING_KEY -> null
                        else -> prevKey
                    }
                },
                nextKey = if (response.posts.isEmpty()) null else range.last + 1
            )
        } catch (e: IOException) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}