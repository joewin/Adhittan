package com.msis.adhittan.logic

import com.msis.adhittan.data.model.NawinDay

object NawinLogic {
    
    // Base sequence for Column 1
    private val BASE_SEQUENCE = listOf(2, 9, 4, 7, 5, 3, 6, 1, 8)
    
    private val ATTRIBUTE_DATA = mapOf(
        1 to AttributeInfo("Arahant (အရဟံ)", 1, "#FF4C4C"),
        2 to AttributeInfo("Sammasambuddho (သမ္မာသမ္ဗုဒ္ဓေါ)", 2, "#FFFF8D"),
        3 to AttributeInfo("Vijjacarana-Sampanno (ဝိဇ္ဇာစရဏသမ္ပန္နော)", 3, "#FF80AB"),
        4 to AttributeInfo("Sugato (သုဂတော)", 4, "#69F0AE"),
        5 to AttributeInfo("Lokavidu (လောကဝိဒူ)", 5, "#FFD740"),
        6 to AttributeInfo("Anuttaro Purisadhamma-Sarathi (အနုတ္တရော ပုရိသဒမ္မသာရထိ)", 6, "#40C4FF"),
        7 to AttributeInfo("Sattha Deva-Manussanam (သတ္ထာ ဒေဝမနုဿာနံ)", 7, "#B388FF"),
        8 to AttributeInfo("Buddho (ဗုဒ္ဓေါ)", 8, "#795548"),
        9 to AttributeInfo("Bhagava (ဘဂဝါ)", 9, "#9E9E9E")
    )

    private data class AttributeInfo(
        val name: String,
        val rounds: Int,
        val colorHex: String
    )

    /**
     * Generates the full 81-day sequence (9 columns x 9 days).
     * Based on user matrix where each column starts with the next ID (1, 2, 3...) 
     * and follows the cyclic shift of the base sequence pattern.
     */
    fun generateSequence(): List<NawinDay> {
        val fullSequence = mutableListOf<Int>()
        
        // We have 9 columns (rounds)
        for (colIndex in 0 until 9) {
            // Column 1 starts with ID 2, Column 2 starts with ID 3... Column 8 starts with 9, Column 9 starts with 1.
            // This is equivalent to (colIndex + 1) % 9 + 1 (wait, let's map it correctly)
            // Column 1 (Mon): Starts with 2
            // Column 2 (Wed): Starts with 3
            // ...
            // Column 8 (Sun): Starts with 9
            // Column 9 (Tue): Starts with 1
            
            val startId = (colIndex + 1) % 9 + 1
            
            // The sequence for each column is a cyclic shift of the BASE_SEQUENCE
            // Let's find the start position of startId in BASE_SEQUENCE
            val baseStartIndex = BASE_SEQUENCE.indexOf(startId)
            
            for (i in 0 until 9) {
                val elementIndex = (baseStartIndex + i) % 9
                fullSequence.add(BASE_SEQUENCE[elementIndex])
            }
        }

        return fullSequence.mapIndexed { index, id ->
            val info = ATTRIBUTE_DATA[id]!!
            NawinDay(
                dayOfWeek = id,
                mantraCount = info.rounds * 108,
                offering = "Chant Mantra",
                colorHex = info.colorHex,
                attributeName = info.name
            )
        }
    }
}
