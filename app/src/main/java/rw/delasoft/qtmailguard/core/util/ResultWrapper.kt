package rw.delasoft.qtmailguard.core.util

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val exception: AppException) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun exceptionOrNull(): AppException? = when (this) {
        is Success -> null
        is Error -> exception
    }

    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppException) -> Unit): AppResult<T> {
        if (this is Error) action(exception)
        return this
    }

    companion object {
        inline fun <T> runCatching(block: () -> T): AppResult<T> {
            return try {
                Success(block())
            } catch (e: AppException) {
                Error(e)
            } catch (e: Exception) {
                Error(AppException.Unknown(e.message ?: "Unknown error", e))
            }
        }
    }
}

sealed class AppException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class FileReadError(message: String, cause: Throwable? = null) :
        AppException(message, cause)

    class ParseError(message: String, cause: Throwable? = null) :
        AppException(message, cause)

    class DatabaseError(message: String, cause: Throwable? = null) :
        AppException(message, cause)

    class VerificationError(message: String, cause: Throwable? = null) :
        AppException(message, cause)

    class Unknown(message: String, cause: Throwable? = null) :
        AppException(message, cause)
}
