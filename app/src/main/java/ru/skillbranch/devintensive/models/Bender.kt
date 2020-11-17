package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when(question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val validateResult = validateAnswer(answer)
        if (validateResult.isNotEmpty()) {
            return "$validateResult\n${question.question}" to status.color
        }
        return if (question.answers.contains(answer.toLowerCase())) {
            question = question.nextQuestion()
            if (question == Question.IDLE) {
                "Отлично - ты справился\nНа этом все, вопросов больше нет" to status.color
            } else {
                "Отлично - ты справился\n${question.question}" to status.color
            }
        } else if (question == Question.IDLE) {
            "На этом все, вопросов больше нет" to status.color
        } else {
            if (status == Status.CRITICAL) {
                question = Question.NAME
                status = Status.NORMAL
                "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            } else {
                status = status.nextStatus()
                "Это неправильный ответ\n${question.question}" to status.color
            }
        }
    }

    private fun validateAnswer(answer: String): String =
         when(question) {
            Question.NAME -> {
                if (answer.isNotEmpty() && answer[0].isUpperCase()) "" else "Имя должно начинаться с заглавной буквы"
            }
            Question.PROFESSION -> {
                if (answer.isNotEmpty() && answer[0].isUpperCase()) "Профессия должна начинаться со строчной буквы" else ""
            }
            Question.MATERIAL -> {
                // \d = [0..9] любая цифрв
                if (answer.contains(Regex("""\d"""))) "Материал не должен содержать цифр" else ""
            }
            Question.BDAY -> {
                // \D = [^0..9] любая не цифра
                if (answer.contains(Regex("""\D"""))) "Год моего рождения должен содержать только цифры" else ""
            }
            Question.SERIAL -> {
                if (answer.length != 7 || answer.contains(Regex("""\D"""))) "Серийный номер содержит только цифры, и их 7" else ""
            }
            Question.IDLE -> {
                ""
            }
        }


    enum class Status(var color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),     // белый
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));     // красный

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(var question: String, var answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender"))  {
            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
    }
}