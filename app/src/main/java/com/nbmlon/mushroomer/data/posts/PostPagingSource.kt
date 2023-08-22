package com.nbmlon.mushroomer.data.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.BoardType
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(
    val backend: PostsService,
    val query: String,
    val boardType: BoardType
) : PagingSource<Int, Post>() {
    var tmpPageNum = 1
    override suspend fun load(
        params: PagingSource.LoadParams<Int>,
    ): PagingSource.LoadResult<Int, Post> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            //val response = backend.getDogam(query, nextPageNumber)

            val response = PostsResponse(0, Post.getDummys(boardType))
            return PagingSource.LoadResult.Page(
                data = response.items,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPage
            )
        } catch (e: IOException) {
            // IOException for network failures.
            return PagingSource.LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return PagingSource.LoadResult.Error(e)
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return PagingSource.LoadResult.Error(e)
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
}