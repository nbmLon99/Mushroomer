package com.nbmlon.mushroomer.data.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.BoardType
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.max

private const val STARTING_KEY = 0


/**
 * @param backend   sevice
 * @param query     searchKeyword
 * @param boardType boardType ( 자유게시판, QnA, 사진 게시판 )
 */
class PostPagingSource(
    val backend: PostsService,
    val boardType: BoardType,
    val query: String?
) : PagingSource<Int, Post>() {
    override suspend fun load(
        params: PagingSource.LoadParams<Int>,
    ): PagingSource.LoadResult<Int, Post> {
        try {
            // Start refresh at page 1 if undefined.
            val startKey = params.key ?: STARTING_KEY
            val range = startKey.until(startKey + params.loadSize)
            //val response = backend.getDogam(query, nextPageNumber)
            val response = PostsResponse( Post.getDummys(boardType) )
            return LoadResult.Page(
                data = response.items,
                prevKey = when (startKey) {
                    STARTING_KEY -> null
                    else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
                        // We're at the start, there's nothing more to load
                        STARTING_KEY -> null
                        else -> prevKey
                    }
                },
                nextKey = range.last + 1
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