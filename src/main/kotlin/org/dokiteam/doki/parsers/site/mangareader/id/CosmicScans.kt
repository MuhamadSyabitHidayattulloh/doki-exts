package org.dokiteam.doki.parsers.site.mangareader.id

import org.dokiteam.doki.parsers.MangaLoaderContext
import org.dokiteam.doki.parsers.MangaSourceParser
import org.dokiteam.doki.parsers.model.Manga
import org.dokiteam.doki.parsers.model.MangaChapter
import org.dokiteam.doki.parsers.model.MangaParserSource
import org.dokiteam.doki.parsers.site.mangareader.MangaReaderParser
import org.dokiteam.doki.parsers.util.*
import java.text.SimpleDateFormat
import java.util.*

@MangaSourceParser("COSMIC_SCANS", "CosmicScans.id", "id")
internal class CosmicScans(context: MangaLoaderContext) :
	MangaReaderParser(context, MangaParserSource.COSMIC_SCANS, "cosmic345.co", pageSize = 30, searchPageSize = 30) {
	override val sourceLocale: Locale = Locale.ENGLISH

	override suspend fun getDetails(manga: Manga): Manga {
		val docs = webClient.httpGet(manga.url.toAbsoluteUrl(domain)).parseHtml()
		val dateFormat = SimpleDateFormat(datePattern, sourceLocale)

		val chapters = docs.select("div.eplister ul.clstyle > li").mapChapters(reversed = true) { index, element ->
			val a = element.selectFirst("div.eph-num > a") ?: return@mapChapters null
			val url = a.attrAsRelativeUrl("href")
			MangaChapter(
				id = generateUid(url),
				title = a.selectFirst("span.chapternum")?.textOrNull(),
				url = url,
				number = index + 1f,
				volume = 0,
				scanlator = null,
				uploadDate = dateFormat.parseSafe(a.selectFirst("span.chapterdate")?.text()),
				branch = null,
				source = source,
			)
		}
		return parseInfo(docs, manga, chapters)
	}
}
