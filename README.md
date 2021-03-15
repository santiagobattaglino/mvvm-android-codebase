Api.kt

@GET("api/v1/users")
    suspend fun getUsers(): NetworkResponse<List<User>, ErrorResponse>

Create Entity Class
Create ViewModel
Create Repo interface and Room Impl
Create DAO
add Entity and DAOto Database.kt
roomModule.kt Inject Api and DAO to the Repo
viewModelsModule.kt Inject Repo to ViewModel