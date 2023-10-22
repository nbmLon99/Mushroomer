package com.nbmlon.mushroomer.data.dogam

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nbmlon.mushroomer.api.service.MushroomService
import com.nbmlon.mushroomer.domain.DogamUseCaseReqeust
import com.nbmlon.mushroomer.domain.toDogamDomain
import com.nbmlon.mushroomer.model.Mushroom
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException
import kotlin.math.max

private const val STARTING_KEY = 0

/** **
 * @param query : 검색어
 * @param sortingOption : 정렬 기준
 */
class DogamPagingSource(
    val items : List<Mushroom>
) : PagingSource<Int, Mushroom>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Mushroom> {
        try {
            // Start refresh at page 1 if undefined.
            val startKey = params.key ?: STARTING_KEY
            val range = startKey.until(startKey + params.loadSize)

            val data = items.subList(startKey, startKey + params.loadSize)

            //val response = DogamResponse(Dogam.getDummy(3,query) )
            return LoadResult.Page(
                data = data,
                prevKey = when (startKey) {
                    STARTING_KEY -> null
                    else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
                        // We're at the start, there's nothing more to load
                        STARTING_KEY -> null
                        else -> prevKey
                    }
                },
                nextKey = if (startKey + params.loadSize >= items.size) null else range.last + 1
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

    override fun getRefreshKey(state: PagingState<Int, Mushroom>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            ensureValidKey( anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1) ?: STARTING_KEY )
        }
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}