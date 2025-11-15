package com.example.metafora.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.metafora.data.db.AppDb
import com.example.metafora.data.model.Question
import com.example.metafora.data.net.NetModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val context: Context) {
    private val db = AppDb.get(context)

    // локальный индекс для офлайн-режима
    private var localIndex = 0

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun nextQuestion(lang: String? = null): Question = withContext(Dispatchers.IO) {
        // 1) Пробуем онлайн
        if (isOnline()) {
            runCatching { NetModule.api.getRandom(lang) }
                .onSuccess { return@withContext it }
        }

        // 2) Оффлайн: работаем с локальной БД
        val dao = db.questionDao()

        // если БД пустая — один раз засеваем
        if (dao.count() == 0) {
            seed()    // просто заполняем БД
        }

        val all = dao.getAll()
        if (all.isEmpty()) {
            error("No questions in local DB")
        }

        // выбираем «следующий» вопрос по индексу
        val q = all[localIndex % all.size]
        localIndex++

        q
    }

    private suspend fun seed() {
        val samples = listOf(
            // 1
            Question(images = listOf("res://book", "res://lightbulb"), help = "Bilim — nur", answer = "Bilim insonni yoritadi"),
            // 2
            Question(images = listOf("res://key", "res://road"), help = "Kalit — yo‘l", answer = "Bilim hayot eshigini ochadi"),
            // 3
            Question(images = listOf("res://bridge", "res://people"), help = "Ko‘prik — birlik", answer = "Insonlar hamkorlik orqali bog‘lanadi"),
            // 4
            Question(images = listOf("res://lighthouse", "res://ship"), help = "Umid — mayoq", answer = "Umid yo‘lni yoritadi"),
            // 5
            Question(images = listOf("res://anchor", "res://waves"), help = "Sabr — yakor", answer = "Sabr insonni barqaror tutadi"),
            // 6
            Question(images = listOf("res://home", "res://shield"), help = "Oila — qalqon", answer = "Oila himoya manbai"),
            // 7
            Question(images = listOf("res://clouds", "res://rocket", "res://star"), help = "Orzu — parvoz", answer = "Orzular osmonga olib chiqadi"),
            // 8
            Question(
                images = listOf("res://iceberg_top", "res://eye"),
                help = "Fikr — chuqur",
                answer = "Har bir fikrning tubi bor"
            ),
            // 9
            Question(
                images = listOf("res://seedling", "res://sun"),
                help = "Mehr — o‘sish",
                answer = "Mehr hayotni oziqlantiradi"
            ),
            // 10
            Question(
                images = listOf("res://scales", "res://feather"),
                help = "Adolat — muvozanat",
                answer = "Adolat — hayot tengligi"
            ),
            // 11
            Question(
                images = listOf("res://stone", "res://document"),
                help = "So‘z — mas’uliyat",
                answer = "Har so‘z og‘ir mas’uliyat"
            ),
            // 12
            Question(
                images = listOf("res://mask", "res://smile", "res://tear"),
                help = "Yuz — niqob",
                answer = "Tabassum orqasida hislar yashirin"
            ),
            // 13
            Question(
                images = listOf("res://mirror", "res://reflection"),
                help = "Oyna — haqiqat",
                answer = "Oyna o‘zligimizni ko‘rsatadi"
            ),
            // 14
            Question(
                images = listOf("res://labyrinth", "res://torch"),
                help = "Bilim — mash’al",
                answer = "Bilim qorong‘ilikni yoritadi"
            ),
            // 15
            Question(
                images = listOf("res://thread", "res://exit"),
                help = "Yo‘l — topildi",
                answer = "Har muammoga yechim bor"
            ),
            // 16
            Question(
                images = listOf("res://people", "res://handshake"),
                help = "Do‘stlik — kuch",
                answer = "Birlikda yengish oson"
            ),
            // 17
            Question(
                images = listOf("res://globe", "res://bridge"),
                help = "Dunyo — ko‘prik",
                answer = "Bilim xalqlarni birlashtiradi"
            ),
            // 18
            Question(
                images = listOf("res://storm", "res://lighthouse"),
                help = "Maqsad — yulduz",
                answer = "Maqsad yo‘lni ko‘rsatadi"
            ),
            // 19
            Question(
                images = listOf("res://feather", "res://stone"),
                help = "Yengillik — kuch",
                answer = "Yumshoqlikda qudrat bor"
            ),
            // 20
            Question(
                images = listOf("res://seedling", "res://soil"),
                help = "Tuproq — hayot",
                answer = "Asos mustahkam bo‘lsa, o‘sish barqaror"
            ),
            // 21
            Question(
                images = listOf("res://sun", "res://watering_can"),
                help = "G‘amxo‘rlik — nur",
                answer = "Mehr bilan hamma narsa yashnaydi"
            ),
            // 22
            Question(
                images = listOf("res://anchor", "res://home"),
                help = "Tinchlik — manzil",
                answer = "Oila — osoyishtalik uyi"
            ),
            // 23
            Question(
                images = listOf("res://book", "res://key"),
                help = "Kitob — kalit",
                answer = "Bilim eshiklarni ochadi"
            ),
            // 24
            Question(
                images = listOf("res://mask", "res://theater"),
                help = "Hayot — sahna",
                answer = "Har kim o‘z rolini o‘ynaydi"
            ),
            // 25
            Question(
                images = listOf("res://mirror", "res://question"),
                help = "O‘zlik — savol",
                answer = "O‘zini tanish — eng muhim javob"
            ),
            // 26
            Question(
                images = listOf("res://labyrinth", "res://exit"),
                help = "Sabr — yo‘l",
                answer = "Sabr yo‘lni topadi"
            ),
            // 27
            Question(
                images = listOf("res://rocket", "res://target"),
                help = "Orzu — nishon",
                answer = "Maqsadga intilish — muvaffaqiyat yo‘li"
            ),
            // 28
            Question(
                images = listOf("res://scales", "res://stone"),
                help = "Haqqoniyat — tayanch",
                answer = "Adolat hayotning ustuni"
            ),
            // 29
            Question(
                images = listOf("res://ship", "res://storm"),
                help = "Sadoqat — kema",
                answer = "Ishonch to‘lqinlarni yengadi"
            ),
            // 30
            Question(
                images = listOf("res://lightbulb", "res://thought"),
                help = "G‘oya — uchqun",
                answer = "G‘oya harakatni uyg‘otadi"
            ),
            // davom: 31–100
            Question(images = listOf("res://book", "res://key"), help = "Bilim — kalit", answer = "Bilim eshiklarni ochadi"),
            Question(images = listOf("res://sun", "res://seedling"), help = "Quyosh — hayot", answer = "Nur o‘sishni ta’minlaydi"),
            Question(images = listOf("res://feather", "res://stone"), help = "Yengillik — kuch", answer = "Yumshoqlikda qudrat bor"),
            Question(images = listOf("res://bridge", "res://globe"), help = "Ko‘prik — do‘stlik", answer = "Birlik xalqlarni bog‘laydi"),
            Question(images = listOf("res://mask", "res://theater"), help = "Hayot — sahna", answer = "Har kim o‘z rolida"),
            Question(images = listOf("res://mirror", "res://person"), help = "Aks — haqiqat", answer = "Oyna o‘zlikni ko‘rsatadi"),
            Question(images = listOf("res://rocket", "res://star"), help = "Orzu — osmonda", answer = "Yuksak orzular qanot beradi"),
            Question(images = listOf("res://anchor", "res://ship"), help = "Sadoqat — tayanch", answer = "Ishonch kemani tutadi"),
            Question(images = listOf("res://scales", "res://document"), help = "Haqqoniyat — qonun", answer = "Adolat yurakda"),
            Question(images = listOf("res://seedling", "res://watering_can"), help = "Mehr — suv", answer = "Mehrsiz o‘sish yo‘q"),
            Question(images = listOf("res://storm", "res://ship"), help = "Sinov — yo‘l", answer = "Har sinov saboq beradi"),
            Question(images = listOf("res://thought", "res://lightbulb"), help = "G‘oya — uchqun", answer = "Fikr harakatni boshlaydi"),
            Question(images = listOf("res://labyrinth", "res://thread"), help = "Yo‘l — topiladi", answer = "Har muammoga yechim bor"),
            Question(images = listOf("res://clouds", "res://rocket"), help = "Orzu — parvoz", answer = "Orzular qanot beradi"),
            Question(images = listOf("res://lighthouse", "res://night"), help = "Nur — yo‘l", answer = "Mayoq qorong‘ilikni yengadi"),
            Question(images = listOf("res://home", "res://shield"), help = "Oila — tinchlik", answer = "Uy — osoyishtalik maskani"),
            Question(images = listOf("res://book", "res://lightbulb"), help = "Kitob — nur", answer = "Bilim — yo‘lchi yulduz"),
            Question(images = listOf("res://waves", "res://anchor"), help = "Sabr — barqarorlik", answer = "Sabr — kuch manbai"),
            Question(images = listOf("res://bridge", "res://people"), help = "Birlik — kuch", answer = "Birgalikda yengish oson"),
            Question(images = listOf("res://sun", "res://feather"), help = "Yorug‘lik — umid", answer = "Umid yurakka nur beradi"),
            Question(images = listOf("res://mirror", "res://question"), help = "Savol — haqiqat", answer = "O‘ziga savol beruvchi topadi"),
            Question(images = listOf("res://seedling", "res://soil"), help = "Asos — mustahkam", answer = "Ildiz chuqur bo‘lsa, daraxt barqaror"),
            Question(images = listOf("res://storm", "res://lighthouse"), help = "Yo‘l — nurda", answer = "Mayoq yo‘lni ko‘rsatadi"),
            Question(images = listOf("res://mask", "res://tear"), help = "Haqiqat — ko‘zda", answer = "Hislar so‘zsiz gapiradi"),
            Question(images = listOf("res://thread", "res://torch"), help = "Ishonch — yo‘lchi", answer = "Ishonch qo‘rquvni yengadi"),
            Question(images = listOf("res://ship", "res://waves"), help = "Hayot — okean", answer = "Har to‘lqin — tajriba"),
            Question(images = listOf("res://sun", "res://thought"), help = "Yorug‘lik — tafakkur", answer = "Nur — fikrga kuch beradi"),
            Question(images = listOf("res://rocket", "res://target"), help = "Maqsad — osmonda", answer = "Maqsadga intilish yuksaltiradi"),
            Question(images = listOf("res://mirror", "res://eye"), help = "Ko‘z — oyna", answer = "Ko‘z — qalb oynasi"),
            Question(images = listOf("res://scales", "res://feather"), help = "Tenglik — go‘zallik", answer = "Adolat muvozanat yaratadi"),
            Question(images = listOf("res://soil", "res://sun"), help = "O‘sish — mehnat", answer = "Har o‘sish orqasida mehnat bor"),
            Question(images = listOf("res://anchor", "res://home"), help = "Tinchlik — manzil", answer = "Oila barqarorlik ramzi"),
            Question(images = listOf("res://mask", "res://smile"), help = "Yuz — sirdosh", answer = "Tabassum ko‘p narsa aytadi"),
            Question(images = listOf("res://labyrinth", "res://exit"), help = "Chiqish — sabrda", answer = "Har yo‘l sabr bilan topiladi"),
            Question(images = listOf("res://globe", "res://people"), help = "Dunyo — uy", answer = "Biz bir dunyo farzandlarimiz"),
            Question(images = listOf("res://book", "res://thought"), help = "Bilim — fikr", answer = "O‘qish tafakkurni ochadi"),
            Question(images = listOf("res://storm", "res://anchor"), help = "Barqarorlik — kuch", answer = "Sabr bo‘lsa, bo‘ron qo‘rqitmaydi"),
            Question(images = listOf("res://seedling", "res://watering_can"), help = "Mehr — suv", answer = "Mehrsiz hayot quriydi"),
            Question(images = listOf("res://sun", "res://globe"), help = "Nur — hayot", answer = "Quyosh — yashash manbai"),
            Question(images = listOf("res://mask", "res://theater", "res://smile"), help = "Rol — hayot", answer = "Hayot — sahna"),
            Question(images = listOf("res://mirror", "res://reflection", "res://eye"), help = "O‘zlik — haqiqat", answer = "O‘zingni bilish — haqiqatni bilish"),
            Question(images = listOf("res://rocket", "res://clouds", "res://target"), help = "Parvoz — orzu", answer = "Orzular qanot beradi"),
            Question(images = listOf("res://anchor", "res://shield"), help = "Himoya — ishonch", answer = "Ishonch — qalqon"),
            Question(images = listOf("res://scales", "res://stone"), help = "Og‘irlik — adolat", answer = "Adolat har so‘zni tortadi"),
            Question(images = listOf("res://seedling", "res://sun"), help = "Yashnash — mehrda", answer = "Mehr o‘sishni ta’minlaydi"),
            Question(images = listOf("res://storm", "res://ship"), help = "Kurash — hayot", answer = "Hayot harakatda davom"),
            Question(images = listOf("res://thought", "res://lightbulb"), help = "Fikr — nur", answer = "Fikr yo‘lni yoritadi"),
            Question(images = listOf("res://mirror", "res://question"), help = "Savol — javob", answer = "O‘zini so‘ragan topadi"),
            Question(images = listOf("res://bridge", "res://handshake"), help = "Do‘stlik — ko‘prik", answer = "Birlik yuraklarni bog‘laydi"),
            Question(images = listOf("res://rocket", "res://star"), help = "Yulduz — yo‘lchi", answer = "Orzular yo‘lni ko‘rsatadi"),
            Question(images = listOf("res://labyrinth", "res://thread"), help = "Yechim — yo‘l", answer = "Har muammo yechiladi"),
            Question(images = listOf("res://anchor", "res://waves"), help = "Tayanch — sabr", answer = "Sabr — kuch manbai"),
            Question(images = listOf("res://mask", "res://tear"), help = "Ko‘z — so‘zsiz", answer = "Hislar ko‘zdan o‘tadi"),
            Question(images = listOf("res://book", "res://lightbulb"), help = "O‘qish — nur", answer = "Bilim — yo‘lchi yulduz"),
            Question(images = listOf("res://bridge", "res://globe"), help = "Bilim — bog‘", answer = "Ma’rifat dunyoni bog‘laydi"),
            Question(images = listOf("res://mirror", "res://eye"), help = "Ko‘z — aks", answer = "Ko‘z qalbni aks ettiradi"),
            Question(images = listOf("res://sun", "res://thought"), help = "Tafakkur — nur", answer = "Nur — fikrni yoritadi"),
            Question(images = listOf("res://storm", "res://lighthouse"), help = "Mayoq — umid", answer = "Umid qorong‘ilikda yo‘lchi"),
            Question(images = listOf("res://labyrinth", "res://torch"), help = "Mash’al — yo‘l", answer = "Bilim yo‘lni yoritadi"),
            Question(images = listOf("res://scales", "res://document"), help = "Haqqoniyat — ishonch", answer = "Adolat yurakda"),
            Question(images = listOf("res://anchor", "res://home"), help = "Oila — ishonch", answer = "Uy — kuch manbai"),
            Question(images = listOf("res://rocket", "res://target"), help = "Maqsad — kuch", answer = "Intilish yuksaltiradi"),
            Question(images = listOf("res://seedling", "res://sun"), help = "Hayot — o‘sish", answer = "Hayot o‘sishda davom"),
            Question(images = listOf("res://mask", "res://smile"), help = "Tabassum — tilsiz", answer = "Mehr so‘zsiz anglashiladi"),
            Question(images = listOf("res://bridge", "res://people"), help = "Birlik — tinchlik", answer = "Hamkorlik tinchlikni keltiradi"),
            Question(images = listOf("res://lightbulb", "res://thought"), help = "Fikr — kuch", answer = "Fikr harakatni uyg‘otadi"),
            Question(images = listOf("res://storm", "res://ship"), help = "Bo‘ron — sinov", answer = "Sinov bardamlikni o‘rgatadi"),
            Question(images = listOf("res://scales", "res://stone"), help = "Og‘irlik — saboq", answer = "Og‘irlik o‘sishga olib keladi"),
            Question(images = listOf("res://mirror", "res://reflection"), help = "Oyna — yo‘l", answer = "Oyna o‘zlikka yo‘l"),
            Question(images = listOf("res://seedling", "res://watering_can"), help = "Mehr — ildiz", answer = "Mehr ildizni mustahkamlaydi"),
            Question(images = listOf("res://sun", "res://globe"), help = "Quyosh — hayot", answer = "Nur yashashni ta’minlaydi"),
            Question(images = listOf("res://rocket", "res://star"), help = "Parvoz — orzu", answer = "Orzu qanot beradi"),
            Question(images = listOf("res://mask", "res://tear"), help = "Ko‘z — haqiqat", answer = "Ko‘z yolg‘on gapirmaydi"),
            Question(images = listOf("res://labyrinth", "res://exit"), help = "Sabr — chiqish", answer = "Sabr yechimni keltiradi"),
            Question(images = listOf("res://lighthouse", "res://night"), help = "Nur — yo‘l", answer = "Umid qorong‘ulikni yengadi")
        )

        fun difficultyScore(q: Question): Int {
            var s = 0
            val imgs = q.images ?: emptyList()
            val helpText = q.help.orEmpty()
            val answerText = q.answer.orEmpty()

            s += imgs.size
            s += helpText.length / 15
            s += answerText.length / 20

            val abstractWords = listOf(
                "adolat","haqqoniyat","haqiqat","umid","maqsad","tafakkur","o‘zlik",
                "g‘oya","mash’al","nur","muvaffaqiyat","barqarorlik",
                "muvozanat","ishonch","sadoqat","birlik","dunyo","kuch","ruhiy"
            )
            val text = (helpText + " " + answerText).lowercase()
            val abstractHits = abstractWords.count { it in text }
            s += abstractHits * 2

            val metaphorHints = listOf("labyrinth","mayoq","mirror","reflection","sahna","ko‘prik","yulduz","parvoz")
            val metaphorHits = metaphorHints.count { it in text }
            s += metaphorHits

            return s
        }

        val sortedSamples = samples.sortedBy { difficultyScore(it) }
        db.questionDao().insertAll(sortedSamples)
    }

    suspend fun addLocal(question: Question) = db.questionDao().insert(question)
    suspend fun count(): Int = db.questionDao().count()
}