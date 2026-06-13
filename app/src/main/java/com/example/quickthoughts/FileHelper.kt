package com.example.quickthoughts

object FileHelper {
    private const val START_TAG = "<!--DRAFT_START-->"
    private const val END_TAG = "<!--DRAFT_END-->"

    fun extractDraftFromFile(fileText: String): String {
        val startIndex = fileText.indexOf(START_TAG)
        val endIndex = fileText.indexOf(END_TAG)

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return ""
        }

        return fileText.substring(startIndex + START_TAG.length, endIndex).trim()
    }

    fun updateFileWithDraft(fileText: String, newDraft: String): String {
        val startIndex = fileText.indexOf(START_TAG)
        val endIndex = fileText.indexOf(END_TAG)

        val draftBlock = "$START_TAG\n$newDraft\n$END_TAG"

        return if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            // Replace existing draft block
            fileText.replaceRange(startIndex, endIndex + END_TAG.length, draftBlock)
        } else {
            // Append to the end
            if (fileText.isBlank()) {
                draftBlock
            } else {
                "${fileText.trimEnd()}\n\n\n$draftBlock"
            }
        }
    }

    fun commitDraft(fileText: String, timestamp: String): String? {
        val draft = extractDraftFromFile(fileText)
        if (draft.isBlank()) return null

        val startIndex = fileText.indexOf(START_TAG)
        val endIndex = fileText.indexOf(END_TAG)

        // Remove the draft block from its current position
        val textWithoutDraft = if (startIndex != -1 && endIndex != -1) {
            fileText.removeRange(startIndex, endIndex + END_TAG.length).trimEnd()
        } else {
            fileText.trimEnd()
        }

        // Append the committed draft and a new empty draft block
        return "$textWithoutDraft\n$timestamp$draft\n\n$START_TAG\n\n$END_TAG"
    }
}
