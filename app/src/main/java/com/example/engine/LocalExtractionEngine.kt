package com.example.engine

import com.example.model.CustomExtractionRule
import com.example.model.ExtractedItem
import com.example.model.ExtractionCategory
import com.example.model.ExtractionOptions
import java.util.UUID
import java.util.regex.Pattern

object LocalExtractionEngine {

    fun extract(text: String, options: ExtractionOptions): List<ExtractedItem> {
        if (text.isBlank()) return emptyList()

        val foundItems = mutableListOf<ExtractedItem>()

        // 1. References & Citations
        if (options.activeCategories.contains(ExtractionCategory.REFERENCE)) {
            extractReferences(text, foundItems)
        }

        // 2. Quotations with Speaker Attribution
        if (options.activeCategories.contains(ExtractionCategory.QUOTATION)) {
            extractQuotations(text, foundItems)
        }

        // 3. Named Entities (People, Orgs, Locations, Tech)
        if (options.activeCategories.contains(ExtractionCategory.NAMED_ENTITY)) {
            extractNamedEntities(text, foundItems)
        }

        // 4. Metrics & Quantitative Stats
        if (options.activeCategories.contains(ExtractionCategory.METRIC_STAT)) {
            extractMetrics(text, foundItems)
        }

        // 5. Temporal / Dates
        if (options.activeCategories.contains(ExtractionCategory.TEMPORAL_DATE)) {
            extractDates(text, foundItems)
        }

        // 6. Extensible Custom Rules
        if (options.activeCategories.contains(ExtractionCategory.CUSTOM_RULE)) {
            extractCustomRules(text, options.customRules, foundItems)
        }

        // Filter by min confidence and remove exact or nested duplicates
        return deduplicateAndFilter(foundItems, options.minConfidence)
    }

    private fun extractReferences(text: String, items: MutableList<ExtractedItem>) {
        // DOIs
        val doiMatcher = RegexPatternRegistry.DOI_PATTERN.matcher(text)
        while (doiMatcher.find()) {
            val matched = doiMatcher.group()
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "DOI",
                    startIndex = doiMatcher.start(),
                    endIndex = doiMatcher.end(),
                    confidenceScore = 0.98f,
                    metadata = mapOf(
                        "type" to "Digital Object Identifier",
                        "standard" to "ISO 26324",
                        "resolver_url" to if (matched.startsWith("http")) matched else "https://doi.org/$matched"
                    ),
                    contextSnippet = getContextSnippet(text, doiMatcher.start(), doiMatcher.end())
                )
            )
        }

        // arXiv
        val arxivMatcher = RegexPatternRegistry.ARXIV_PATTERN.matcher(text)
        while (arxivMatcher.find()) {
            val matched = arxivMatcher.group()
            val cleanId = matched.replace("arXiv:", "", ignoreCase = true)
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "ARXIV_ID",
                    startIndex = arxivMatcher.start(),
                    endIndex = arxivMatcher.end(),
                    confidenceScore = 0.97f,
                    metadata = mapOf(
                        "type" to "Preprint Repository",
                        "eprint_id" to cleanId,
                        "paper_url" to "https://arxiv.org/abs/$cleanId"
                    ),
                    contextSnippet = getContextSnippet(text, arxivMatcher.start(), arxivMatcher.end())
                )
            )
        }

        // Academic Citations: Author (Year) or (Author, Year)
        val citeMatcher = RegexPatternRegistry.ACADEMIC_CITATION_PATTERN.matcher(text)
        while (citeMatcher.find()) {
            val author = citeMatcher.group(1) ?: citeMatcher.group(3) ?: ""
            val year = citeMatcher.group(2) ?: citeMatcher.group(4) ?: ""
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = citeMatcher.group(),
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "ACADEMIC_CITATION",
                    startIndex = citeMatcher.start(),
                    endIndex = citeMatcher.end(),
                    confidenceScore = 0.94f,
                    metadata = mapOf(
                        "author" to author,
                        "year" to year,
                        "citation_style" to "APA / Harvard Author-Date"
                    ),
                    contextSnippet = getContextSnippet(text, citeMatcher.start(), citeMatcher.end())
                )
            )
        }

        // Bracketed Citations: [1], [12, 15], [3-5]
        val bracketMatcher = RegexPatternRegistry.BRACKETED_CITATION_PATTERN.matcher(text)
        while (bracketMatcher.find()) {
            val matched = bracketMatcher.group()
            val numbers = bracketMatcher.group(1) ?: ""
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "NUMERIC_CITATION",
                    startIndex = bracketMatcher.start(),
                    endIndex = bracketMatcher.end(),
                    confidenceScore = 0.91f,
                    metadata = mapOf(
                        "citation_indices" to numbers,
                        "citation_style" to "IEEE / Vancouver Numeric"
                    ),
                    contextSnippet = getContextSnippet(text, bracketMatcher.start(), bracketMatcher.end())
                )
            )
        }

        // Patents
        val patentMatcher = RegexPatternRegistry.PATENT_PATTERN.matcher(text)
        while (patentMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = patentMatcher.group(),
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "PATENT",
                    startIndex = patentMatcher.start(),
                    endIndex = patentMatcher.end(),
                    confidenceScore = 0.95f,
                    metadata = mapOf("type" to "Patent Identifier"),
                    contextSnippet = getContextSnippet(text, patentMatcher.start(), patentMatcher.end())
                )
            )
        }

        // ISBNs
        val isbnMatcher = RegexPatternRegistry.ISBN_PATTERN.matcher(text)
        while (isbnMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = isbnMatcher.group(),
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "ISBN",
                    startIndex = isbnMatcher.start(),
                    endIndex = isbnMatcher.end(),
                    confidenceScore = 0.96f,
                    metadata = mapOf("type" to "International Standard Book Number"),
                    contextSnippet = getContextSnippet(text, isbnMatcher.start(), isbnMatcher.end())
                )
            )
        }

        // Standards & Regulatory Acts
        val stdMatcher = RegexPatternRegistry.STANDARD_PATTERN.matcher(text)
        while (stdMatcher.find()) {
            val org = stdMatcher.group(1) ?: ""
            val spec = stdMatcher.group(2) ?: ""
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = stdMatcher.group(),
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "TECHNICAL_STANDARD",
                    startIndex = stdMatcher.start(),
                    endIndex = stdMatcher.end(),
                    confidenceScore = 0.92f,
                    metadata = mapOf(
                        "issuing_body" to org,
                        "specification_id" to spec,
                        "compliance_scope" to "Industry Specification / Regulation"
                    ),
                    contextSnippet = getContextSnippet(text, stdMatcher.start(), stdMatcher.end())
                )
            )
        }

        // URLs
        val urlMatcher = RegexPatternRegistry.URL_PATTERN.matcher(text)
        while (urlMatcher.find()) {
            val matched = urlMatcher.group()
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.REFERENCE,
                    subCategory = "WEB_URL",
                    startIndex = urlMatcher.start(),
                    endIndex = urlMatcher.end(),
                    confidenceScore = 0.99f,
                    metadata = mapOf(
                        "protocol" to (if (matched.startsWith("https", ignoreCase = true)) "HTTPS" else "HTTP/Web"),
                        "link" to matched
                    ),
                    contextSnippet = getContextSnippet(text, urlMatcher.start(), urlMatcher.end())
                )
            )
        }
    }

    private fun extractQuotations(text: String, items: MutableList<ExtractedItem>) {
        val matcher = RegexPatternRegistry.QUOTATION_PATTERN.matcher(text)
        while (matcher.find()) {
            val fullMatch = matcher.group()
            val quoteContent = matcher.group(1)?.trim() ?: fullMatch
            val start = matcher.start()
            val end = matcher.end()

            val attribution = detectAttribution(text, start, end)

            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = fullMatch,
                    category = ExtractionCategory.QUOTATION,
                    subCategory = "DIRECT_SPEECH",
                    startIndex = start,
                    endIndex = end,
                    confidenceScore = if (attribution["speaker"] != null) 0.95f else 0.88f,
                    metadata = buildMap {
                        put("length_chars", quoteContent.length.toString())
                        put("word_count", quoteContent.split(Regex("\\s+")).size.toString())
                        attribution["speaker"]?.let { put("attributed_speaker", it) }
                        attribution["verb"]?.let { put("attribution_verb", it) }
                    },
                    contextSnippet = getContextSnippet(text, start, end)
                )
            )
        }
    }

    private fun detectAttribution(text: String, quoteStart: Int, quoteEnd: Int): Map<String, String> {
        val result = mutableMapOf<String, String>()

        val beforeStart = (quoteStart - 90).coerceAtLeast(0)
        val beforeText = text.substring(beforeStart, quoteStart)

        val afterEnd = (quoteEnd + 90).coerceAtMost(text.length)
        val afterText = text.substring(quoteEnd, afterEnd)

        val verbPattern = "(?:said|stated|remarked|declared|noted|commented|argued|observed|whispered|added|announced|explained|claimed|wrote|reported|cited|expressed|emphasized|insisted|warned|suggested|tweeted|confirmed|pointed\\s+out|told)"

        // Case 1: [Speaker] [verb] after quote, e.g. "...," Pichai declared
        val afterMatcher = Pattern.compile("([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s+($verbPattern)", Pattern.CASE_INSENSITIVE).matcher(afterText)
        if (afterMatcher.find()) {
            result["speaker"] = afterMatcher.group(1)?.trim() ?: ""
            result["verb"] = afterMatcher.group(2)?.trim() ?: ""
            return result
        }

        // Case 2: [verb] [Speaker] after quote, e.g. "...," said Dr. Elena Rostova
        val afterVerbFirst = Pattern.compile("($verbPattern)\\s+((?:Dr\\.|Prof\\.|Mr\\.|Ms\\.|Mrs\\.|CEO|CTO)?\\s*[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)", Pattern.CASE_INSENSITIVE).matcher(afterText)
        if (afterVerbFirst.find()) {
            result["verb"] = afterVerbFirst.group(1)?.trim() ?: ""
            result["speaker"] = afterVerbFirst.group(2)?.trim() ?: ""
            return result
        }

        // Case 3: According to / As [Speaker] [verb] before quote
        val beforeMatcher = Pattern.compile("(?:As|When|According\\s+to)\\s+((?:Dr\\.|Prof\\.|Mr\\.|Ms\\.|CEO|CTO)?\\s*[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s*($verbPattern)?", Pattern.CASE_INSENSITIVE).matcher(beforeText)
        if (beforeMatcher.find()) {
            result["speaker"] = beforeMatcher.group(1)?.trim() ?: ""
            beforeMatcher.group(2)?.let { result["verb"] = it.trim() }
            return result
        }

        // Case 4: General [Speaker] [verb]: before quote
        val generalBefore = Pattern.compile("([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s+($verbPattern)\\s*:", Pattern.CASE_INSENSITIVE).matcher(beforeText)
        if (generalBefore.find()) {
            result["speaker"] = generalBefore.group(1)?.trim() ?: ""
            result["verb"] = generalBefore.group(2)?.trim() ?: ""
            return result
        }

        return result
    }

    private fun extractNamedEntities(text: String, items: MutableList<ExtractedItem>) {
        // 1. People with honorifics or titles
        val personTitleMatcher = RegexPatternRegistry.PERSON_WITH_TITLE_PATTERN.matcher(text)
        while (personTitleMatcher.find()) {
            val matched = personTitleMatcher.group(1) ?: personTitleMatcher.group()
            if (matched.isNullOrBlank()) continue
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.NAMED_ENTITY,
                    subCategory = "PERSON",
                    startIndex = personTitleMatcher.start(),
                    endIndex = personTitleMatcher.end(),
                    confidenceScore = 0.95f,
                    metadata = mapOf(
                        "entity_type" to "PERSON",
                        "title_honorific" to matched.substringBefore(" ").trim()
                    ),
                    contextSnippet = getContextSnippet(text, personTitleMatcher.start(), personTitleMatcher.end())
                )
            )
        }

        // 2. General People Names (First Last)
        val personNameMatcher = RegexPatternRegistry.GENERAL_PERSON_NAME_PATTERN.matcher(text)
        while (personNameMatcher.find()) {
            val matched = personNameMatcher.group()
            val firstWord = matched.substringBefore(" ")
            val lastWord = matched.substringAfterLast(" ")
            if (!RegexPatternRegistry.NON_NAME_WORDS.contains(firstWord) &&
                !RegexPatternRegistry.NON_NAME_WORDS.contains(lastWord)) {
                items.add(
                    ExtractedItem(
                        id = UUID.randomUUID().toString(),
                        text = matched,
                        category = ExtractionCategory.NAMED_ENTITY,
                        subCategory = "PERSON",
                        startIndex = personNameMatcher.start(),
                        endIndex = personNameMatcher.end(),
                        confidenceScore = 0.86f,
                        metadata = mapOf(
                            "entity_type" to "PERSON",
                            "name" to matched
                        ),
                        contextSnippet = getContextSnippet(text, personNameMatcher.start(), personNameMatcher.end())
                    )
                )
            }
        }

        // 3. Known organizations dictionary
        for (org in RegexPatternRegistry.KNOWN_ORGANIZATIONS) {
            val orgPattern = Pattern.compile("\\b${Pattern.quote(org)}\\b")
            val orgMatcher = orgPattern.matcher(text)
            while (orgMatcher.find()) {
                items.add(
                    ExtractedItem(
                        id = UUID.randomUUID().toString(),
                        text = orgMatcher.group(),
                        category = ExtractionCategory.NAMED_ENTITY,
                        subCategory = "ORGANIZATION",
                        startIndex = orgMatcher.start(),
                        endIndex = orgMatcher.end(),
                        confidenceScore = 0.96f,
                        metadata = mapOf(
                            "entity_type" to "ORGANIZATION",
                            "source" to "Dictionary"
                        ),
                        contextSnippet = getContextSnippet(text, orgMatcher.start(), orgMatcher.end())
                    )
                )
            }
        }

        // 4. Organization Suffix Pattern (e.g., Acme Inc, Harvard University)
        val orgSuffixMatcher = RegexPatternRegistry.ORG_SUFFIX_PATTERN.matcher(text)
        while (orgSuffixMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = orgSuffixMatcher.group(),
                    category = ExtractionCategory.NAMED_ENTITY,
                    subCategory = "ORGANIZATION",
                    startIndex = orgSuffixMatcher.start(),
                    endIndex = orgSuffixMatcher.end(),
                    confidenceScore = 0.92f,
                    metadata = mapOf(
                        "entity_type" to "ORGANIZATION",
                        "pattern" to "Corporate/Institutional Suffix"
                    ),
                    contextSnippet = getContextSnippet(text, orgSuffixMatcher.start(), orgSuffixMatcher.end())
                )
            )
        }

        // 5. Known locations dictionary
        for (loc in RegexPatternRegistry.KNOWN_LOCATIONS) {
            val locPattern = Pattern.compile("\\b${Pattern.quote(loc)}\\b")
            val locMatcher = locPattern.matcher(text)
            while (locMatcher.find()) {
                items.add(
                    ExtractedItem(
                        id = UUID.randomUUID().toString(),
                        text = locMatcher.group(),
                        category = ExtractionCategory.NAMED_ENTITY,
                        subCategory = "LOCATION",
                        startIndex = locMatcher.start(),
                        endIndex = locMatcher.end(),
                        confidenceScore = 0.93f,
                        metadata = mapOf(
                            "entity_type" to "LOCATION",
                            "source" to "Geographic Database"
                        ),
                        contextSnippet = getContextSnippet(text, locMatcher.start(), locMatcher.end())
                    )
                )
            }
        }

        // 6. Location Suffix / Region Pattern
        val locSuffixMatcher = RegexPatternRegistry.LOCATION_SUFFIX_PATTERN.matcher(text)
        while (locSuffixMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = locSuffixMatcher.group(),
                    category = ExtractionCategory.NAMED_ENTITY,
                    subCategory = "LOCATION",
                    startIndex = locSuffixMatcher.start(),
                    endIndex = locSuffixMatcher.end(),
                    confidenceScore = 0.90f,
                    metadata = mapOf(
                        "entity_type" to "LOCATION",
                        "pattern" to "Geographic Descriptor"
                    ),
                    contextSnippet = getContextSnippet(text, locSuffixMatcher.start(), locSuffixMatcher.end())
                )
            )
        }

        // 7. Technology & Scientific Concepts dictionary
        for (tech in RegexPatternRegistry.KNOWN_TECHNOLOGIES) {
            val techPattern = Pattern.compile("\\b${Pattern.quote(tech)}\\b", Pattern.CASE_INSENSITIVE)
            val techMatcher = techPattern.matcher(text)
            while (techMatcher.find()) {
                items.add(
                    ExtractedItem(
                        id = UUID.randomUUID().toString(),
                        text = techMatcher.group(),
                        category = ExtractionCategory.NAMED_ENTITY,
                        subCategory = "TECHNOLOGY",
                        startIndex = techMatcher.start(),
                        endIndex = techMatcher.end(),
                        confidenceScore = 0.89f,
                        metadata = mapOf(
                            "entity_type" to "TECHNOLOGY / CONCEPT",
                            "domain" to "Computer Science / Tech"
                        ),
                        contextSnippet = getContextSnippet(text, techMatcher.start(), techMatcher.end())
                    )
                )
            }
        }
    }

    private fun extractMetrics(text: String, items: MutableList<ExtractedItem>) {
        // Currencies
        val currMatcher = RegexPatternRegistry.CURRENCY_PATTERN.matcher(text)
        while (currMatcher.find()) {
            val matched = currMatcher.group()
            val symbol = when {
                matched.contains("$") -> "USD"
                matched.contains("€") -> "EUR"
                matched.contains("£") -> "GBP"
                matched.contains("¥") -> "JPY"
                matched.contains("₹") -> "INR"
                matched.contains("CAD", ignoreCase = true) -> "CAD"
                matched.contains("AUD", ignoreCase = true) -> "AUD"
                else -> "CURRENCY"
            }
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.METRIC_STAT,
                    subCategory = "FINANCIAL_CURRENCY",
                    startIndex = currMatcher.start(),
                    endIndex = currMatcher.end(),
                    confidenceScore = 0.97f,
                    metadata = mapOf(
                        "currency_symbol" to symbol,
                        "unit_type" to "Monetary Value"
                    ),
                    contextSnippet = getContextSnippet(text, currMatcher.start(), currMatcher.end())
                )
            )
        }

        // Percentages
        val pctMatcher = RegexPatternRegistry.PERCENTAGE_PATTERN.matcher(text)
        while (pctMatcher.find()) {
            val matched = pctMatcher.group()
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.METRIC_STAT,
                    subCategory = "PERCENTAGE",
                    startIndex = pctMatcher.start(),
                    endIndex = pctMatcher.end(),
                    confidenceScore = 0.98f,
                    metadata = mapOf(
                        "unit" to "Percent (%)",
                        "raw_value" to matched.replace("%", "").replace("percent", "", ignoreCase = true).trim()
                    ),
                    contextSnippet = getContextSnippet(text, pctMatcher.start(), pctMatcher.end())
                )
            )
        }

        // Metric measurements & units
        val metricMatcher = RegexPatternRegistry.METRIC_PATTERN.matcher(text)
        while (metricMatcher.find()) {
            val matched = metricMatcher.group()
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = matched,
                    category = ExtractionCategory.METRIC_STAT,
                    subCategory = "QUANTITATIVE_METRIC",
                    startIndex = metricMatcher.start(),
                    endIndex = metricMatcher.end(),
                    confidenceScore = 0.91f,
                    metadata = mapOf(
                        "unit_classification" to "Measurement Unit / Count"
                    ),
                    contextSnippet = getContextSnippet(text, metricMatcher.start(), metricMatcher.end())
                )
            )
        }
    }

    private fun extractDates(text: String, items: MutableList<ExtractedItem>) {
        // Full dates: October 24, 2024 / 24 October 2024
        val fullMatcher = RegexPatternRegistry.FULL_DATE_PATTERN.matcher(text)
        while (fullMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = fullMatcher.group(),
                    category = ExtractionCategory.TEMPORAL_DATE,
                    subCategory = "CALENDAR_DATE",
                    startIndex = fullMatcher.start(),
                    endIndex = fullMatcher.end(),
                    confidenceScore = 0.98f,
                    metadata = mapOf(
                        "format" to "Explicit Date",
                        "temporal_type" to "Calendar Date"
                    ),
                    contextSnippet = getContextSnippet(text, fullMatcher.start(), fullMatcher.end())
                )
            )
        }

        // Month + Year: October 2024
        val monthYearMatcher = RegexPatternRegistry.MONTH_YEAR_PATTERN.matcher(text)
        while (monthYearMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = monthYearMatcher.group(),
                    category = ExtractionCategory.TEMPORAL_DATE,
                    subCategory = "MONTH_YEAR",
                    startIndex = monthYearMatcher.start(),
                    endIndex = monthYearMatcher.end(),
                    confidenceScore = 0.94f,
                    metadata = mapOf("temporal_type" to "Month and Year"),
                    contextSnippet = getContextSnippet(text, monthYearMatcher.start(), monthYearMatcher.end())
                )
            )
        }

        // Quarters: Q3 2023, FY2024
        val qMatcher = RegexPatternRegistry.QUARTER_DATE_PATTERN.matcher(text)
        while (qMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = qMatcher.group(),
                    category = ExtractionCategory.TEMPORAL_DATE,
                    subCategory = "FISCAL_QUARTER",
                    startIndex = qMatcher.start(),
                    endIndex = qMatcher.end(),
                    confidenceScore = 0.95f,
                    metadata = mapOf("temporal_type" to "Fiscal Reporting Era"),
                    contextSnippet = getContextSnippet(text, qMatcher.start(), qMatcher.end())
                )
            )
        }

        // Numeric Dates: 2024-10-24, 10/24/2024
        val numDateMatcher = RegexPatternRegistry.NUMERIC_DATE_PATTERN.matcher(text)
        while (numDateMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = numDateMatcher.group(),
                    category = ExtractionCategory.TEMPORAL_DATE,
                    subCategory = "NUMERIC_DATE",
                    startIndex = numDateMatcher.start(),
                    endIndex = numDateMatcher.end(),
                    confidenceScore = 0.96f,
                    metadata = mapOf("temporal_type" to "Formatted Numeric Date"),
                    contextSnippet = getContextSnippet(text, numDateMatcher.start(), numDateMatcher.end())
                )
            )
        }

        // Context Years: in 2024, since 1999
        val yearMatcher = RegexPatternRegistry.CONTEXT_YEAR_PATTERN.matcher(text)
        while (yearMatcher.find()) {
            items.add(
                ExtractedItem(
                    id = UUID.randomUUID().toString(),
                    text = yearMatcher.group(),
                    category = ExtractionCategory.TEMPORAL_DATE,
                    subCategory = "YEAR",
                    startIndex = yearMatcher.start(),
                    endIndex = yearMatcher.end(),
                    confidenceScore = 0.90f,
                    metadata = mapOf("temporal_type" to "Contextual Year"),
                    contextSnippet = getContextSnippet(text, yearMatcher.start(), yearMatcher.end())
                )
            )
        }
    }

    private fun extractCustomRules(
        text: String,
        rules: List<CustomExtractionRule>,
        items: MutableList<ExtractedItem>
    ) {
        for (rule in rules) {
            if (!rule.isEnabled || rule.regexPattern.isBlank()) continue
            try {
                val pattern = Pattern.compile(rule.regexPattern, Pattern.CASE_INSENSITIVE)
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    items.add(
                        ExtractedItem(
                            id = UUID.randomUUID().toString(),
                            text = matcher.group(),
                            category = ExtractionCategory.CUSTOM_RULE,
                            subCategory = rule.categoryLabel.ifBlank { "CUSTOM" },
                            startIndex = matcher.start(),
                            endIndex = matcher.end(),
                            confidenceScore = rule.defaultConfidence,
                            metadata = mapOf(
                                "rule_name" to rule.name,
                                "pattern" to rule.regexPattern,
                                "description" to rule.description
                            ),
                            contextSnippet = getContextSnippet(text, matcher.start(), matcher.end())
                        )
                    )
                }
            } catch (_: Exception) {
                // Ignore invalid user regex gracefully
            }
        }
    }

    private fun getContextSnippet(text: String, start: Int, end: Int): String {
        val snippetStart = (start - 40).coerceAtLeast(0)
        val snippetEnd = (end + 40).coerceAtMost(text.length)
        val prefix = if (snippetStart > 0) "..." else ""
        val suffix = if (snippetEnd < text.length) "..." else ""
        return "$prefix${text.substring(snippetStart, snippetEnd).trim()}$suffix"
    }

    private fun deduplicateAndFilter(
        items: List<ExtractedItem>,
        minConfidence: Float
    ): List<ExtractedItem> {
        val filtered = items.filter { it.confidenceScore >= minConfidence }

        // Sort by start index ascending, then by length descending, then confidence
        val sorted = filtered.sortedWith(
            compareBy<ExtractedItem> { it.startIndex }
                .thenByDescending { it.endIndex - it.startIndex }
                .thenByDescending { it.confidenceScore }
        )

        val result = mutableListOf<ExtractedItem>()
        for (candidate in sorted) {
            val duplicate = result.any { existing ->
                if (existing.category == candidate.category) {
                    val overlapStart = maxOf(existing.startIndex, candidate.startIndex)
                    val overlapEnd = minOf(existing.endIndex, candidate.endIndex)
                    overlapStart < overlapEnd
                } else {
                    existing.startIndex == candidate.startIndex && existing.endIndex == candidate.endIndex
                }
            }
            if (!duplicate) {
                result.add(candidate)
            }
        }

        return result
    }
}

