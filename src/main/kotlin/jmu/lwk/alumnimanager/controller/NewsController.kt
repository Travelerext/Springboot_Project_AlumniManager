package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.News
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.repository.NewsRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/news")
class NewsController(
    private val newsRepository: NewsRepository,
    private val userRepository: UserRepository
) {

    data class NewsRequest(
        val title: String,
        val content: String,
        val author: String
    )

    data class NewsResponse(
        val id: String,
        val title: String,
        val content: String,
        val author: String,
        val writeDate: Instant
    )

    @GetMapping
    fun getAllNews(@RequestParam loadedPageSize: Int): List<NewsResponse> {
        return newsRepository.findAllByOrderByWriteDateDesc(PageRequest.of(loadedPageSize, 10)).map { it.toResponse() }
    }

    @GetMapping("/{title}")
    fun findNewsByTitle(@PathVariable title: String): List<NewsResponse> {
        return newsRepository.findAllByTitleContaining(title).map { it.toResponse() }
    }

    @GetMapping("/my")
    fun getMyNews(): List<NewsResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != null) {
            return newsRepository.findAllByLaunchId(user.id).map { it.toResponse() }
        } else throw IllegalArgumentException("权限不足")
    }

    @PostMapping
    fun addNews(@RequestBody request: NewsRequest): NewsResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != null) {
            return newsRepository.save(
                News(
                    title = request.title,
                    content = request.content,
                    author = request.author,
                    launchId = user.id
                )
            ).toResponse()
        } else throw IllegalArgumentException("权限不足")
    }

    @PutMapping("/{id}")
    fun updateNews(@PathVariable id: ObjectId, @RequestBody request: NewsRequest): NewsResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        val news = newsRepository.findById(id).orElseThrow{ IllegalArgumentException("新闻不存在") }
        if (user.id == news.launchId) {
            return newsRepository.save(
                news.copy(
                    title = request.title,
                    content = request.content,
                    author = request.author
                )
            ).toResponse()
        } else throw IllegalArgumentException("仅支持发布者修改")
    }

    @DeleteMapping("/{id}")
    fun deleteNews(@PathVariable id: ObjectId) {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        val news = newsRepository.findById(id).orElseThrow{ IllegalArgumentException("新闻不存在") }
        if (user.role == Role.GeneralAdmin || user.id == news.launchId)
            newsRepository.deleteById(news.id)
        else throw IllegalArgumentException("无权修改")
    }

    private fun News.toResponse(): NewsResponse {
        return NewsResponse(
            id = id.toHexString(),
            title = title,
            content = content,
            author = author,
            writeDate = writeDate
        )
    }

}