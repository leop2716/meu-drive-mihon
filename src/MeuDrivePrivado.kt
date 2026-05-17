package eu.kanade.tachiyomi.extension.pt.meudriveprivado

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

class MeuDrivePrivado : HttpSource() {
    override val name = "Meu Drive Privado"
    
    // ⚠️ COLOQUE SEU LINK DO GOOGLE APPS SCRIPT DENTRO DAS ASPAS ABAIXO
    override val baseUrl = "https://script.google.com/macros/s/" 
    
    override val lang = "pt"
    override val supportsLatest = false

    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl?action=list", headers)
    }

    override fun popularMangaParse(response: Response): MangasPage {
        val jsonArray = JSONArray(response.body.string())
        val mangas = mutableListOf<SManga>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            mangas.add(SManga.create().apply {
                url = item.getString("id")
                title = item.getString("title")
                thumbnail_url = item.getString("cover")
            })
        }
        return MangasPage(mangas, false)
    }

    override fun chapterListRequest(manga: SManga): Request {
        return GET("$baseUrl?action=chapters&mangaId=${manga.url}", headers)
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        val jsonArray = JSONArray(response.body.string())
        val chapters = mutableListOf<SChapter>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            chapters.add(SChapter.create().apply {
                url = item.getString("id")
                name = item.getString("name")
                chapter_number = (jsonArray.length() - i).toFloat()
            })
        }
        return chapters
    }

    override fun pageListRequest(chapter: SChapter): Request {
        return GET("$baseUrl?action=pages&chapterId=${chapter.url}", headers)
    }

    override fun pageListParse(response: Response): List<Page> {
        val jsonArray = JSONArray(response.body.string())
        val pages = mutableListOf<Page>()
        for (i in 0 until jsonArray.length()) {
            pages.add(Page(i, "", jsonArray.getString(i)))
        }
        return pages
    }

    override fun mangaDetailsParse(response: Response): SManga = SManga.create()
    override fun latestUpdatesRequest(page: Int): Request = throw Exception("Não usado")
    override fun latestUpdatesParse(response: Response): MangasPage = throw Exception("Não usado")
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request = throw Exception("Não usado")
    override fun searchMangaParse(response: Response): MangasPage = throw Exception("Não usado")
}
