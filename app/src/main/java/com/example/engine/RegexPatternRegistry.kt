package com.example.engine

import java.util.regex.Pattern

object RegexPatternRegistry {
    // DOIs: e.g., 10.1038/s41586-021-03819-2, https://doi.org/10.1016/j.cell.2021.01.001
    val DOI_PATTERN: Pattern = Pattern.compile(
        "(?:https?://(?:dx\\.)?doi\\.org/)?\\b10\\.\\d{4,9}/[-._;()/:A-Za-z0-9]+\\b",
        Pattern.CASE_INSENSITIVE
    )

    // arXiv identifiers: e.g., arXiv:2106.09685, arXiv:2106.09685v2
    val ARXIV_PATTERN: Pattern = Pattern.compile(
        "\\barXiv:\\d{4}\\.\\d{4,5}(?:v\\d+)?\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Academic author-year citations: e.g., Vaswani et al. (2017), (Kaplan et al., 2020), Smith & Jones (2019), (Miller, 2021)
    val ACADEMIC_CITATION_PATTERN: Pattern = Pattern.compile(
        "(?:\\b([A-Z][a-z]+(?:\\s+(?:et\\s+al\\.|and|&)\\s+[A-Z][a-z]+)?)\\s*\\((19\\d{2}|20\\d{2})[a-z]?\\))|(?:\\(([A-Z][a-z]+(?:\\s+(?:et\\s+al\\.|and|&)\\s+[A-Z][a-z]+)?),?\\s*(19\\d{2}|20\\d{2})[a-z]?\\))"
    )

    // Bracketed numeric citations: e.g., [1], [12], [1, 2, 5], [3-7], [12-15]
    val BRACKETED_CITATION_PATTERN: Pattern = Pattern.compile(
        "\\[(\\d{1,4}(?:\\s*[-,\u2013]\\s*\\d{1,4})*)\\]"
    )

    // Patents: e.g., US Patent 10,123,456, Patent No. 8492019
    val PATENT_PATTERN: Pattern = Pattern.compile(
        "\\b(?:US\\s+Patent|Patent\\s+No\\.|Patent)\\s*#?\\s*\\d{1,3}(?:,\\d{3})*\\b",
        Pattern.CASE_INSENSITIVE
    )

    // ISBN: e.g., ISBN 978-3-16-148410-0, ISBN 0-306-40615-2
    val ISBN_PATTERN: Pattern = Pattern.compile(
        "\\bISBN(?:-1[03])?:?\\s*(?=[0-9X]{10}|(?=(?:[0-9]+[- ]){3})[0-9X]{13}|97[89][0-9]{10}|(?=(?:[0-9]+[- ]){4})[0-9]{17})[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Standards & Regulatory codes: e.g., ISO 27001, IEEE 754-2019, RFC 2616, NIST SP 800-53, GDPR Article 17, HIPAA, Directive 95/46/EC, ANSI/IEEE C37.90
    val STANDARD_PATTERN: Pattern = Pattern.compile(
        "\\b(ISO(?:/IEC)?|IEEE(?:\\s+Standard)?|RFC|NIST(?:\\s+SP)?|GDPR|HIPAA|ANSI|ASTM|ETSI|W3C|SOC\\s*[123]|Directive|CFR|Section\\s+508|Case)\\s*[-:]?\\s*([A-Za-z0-9-./:]+(?:\\s+[A-Za-z0-9-.]+)*)\\b",
        Pattern.CASE_INSENSITIVE
    )

    // URLs & Web references
    val URL_PATTERN: Pattern = Pattern.compile(
        "\\b(?:https?://|ftp://|www\\.)[A-Za-z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]+\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Direct Quotations: e.g. "...", “...”, ‘...’
    val QUOTATION_PATTERN: Pattern = Pattern.compile(
        "[\"“‘«]([^\"”’»]{3,400})[\"”’»]"
    )

    // Currency values: e.g., $9.2 billion, €20 million, £500k, USD 1,500, 500 EUR, ¥50,000, ₹1,000
    val CURRENCY_PATTERN: Pattern = Pattern.compile(
        "(?:(?:\\$|€|£|¥|₹|CAD|AUD|USD|EUR|GBP|INR|JPY|CHF|CNY)\\s*\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?|\\b\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?\\s*(?:USD|EUR|GBP|INR|JPY|CAD|AUD|dollars?|euros?|pounds?))\\s*(?:billion|million|trillion|k|B|M)?\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Percentages: e.g., 94.2%, 28.5%, 4%, 1,250%, 25 percent
    val PERCENTAGE_PATTERN: Pattern = Pattern.compile(
        "\\b\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?\\s*(?:%|percent(?:age)?(?:\\s+points)?)\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Numeric metrics and physical / scientific / quantitative units
    val METRIC_PATTERN: Pattern = Pattern.compile(
        "\\b\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?\\s*(?:x|x-fold|fold|parameters|mg|g|kg|lbs?|oz|hours?|hrs?|days?|weeks?|months?|years?|secs?|seconds?|mins?|minutes?|patients?|cases?|users?|people|participants?|km|miles?|meters?|m|cm|mm|GB|MB|TB|KB|PB|ms|Hz|GHz|MHz|volts?|watts?|units?|items?|samples?|fps|mph|kph|dpi|px|pixels?|points?|pts)\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Explicit Full Dates: e.g., October 24, 2024; 24 October 2024; Oct 24, 2024; 24th of October 2024
    val FULL_DATE_PATTERN: Pattern = Pattern.compile(
        "\\b(?:(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)\\s+\\d{1,2}(?:st|nd|rd|th)?,?\\s+\\d{4})|(?:\\d{1,2}(?:st|nd|rd|th)?\\s+(?:of\\s+)?(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec),?\\s+\\d{4})\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Month + Year: e.g., October 2024, Jan 2023
    val MONTH_YEAR_PATTERN: Pattern = Pattern.compile(
        "\\b(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)\\s+\\d{4}\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Quarters & Fiscal Eras: e.g. Q3 2023, Q4 2024, FY2024, FY24
    val QUARTER_DATE_PATTERN: Pattern = Pattern.compile(
        "\\b(?:Q[1-4]\\s+\\d{4}|FY\\s*\\d{2,4})\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Standard Numeric dates: 2024-10-24, 10/24/2024, 24/10/2024, 2024/10/24, 24.10.2024
    val NUMERIC_DATE_PATTERN: Pattern = Pattern.compile(
        "\\b(?:\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}|\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{4})\\b"
    )

    // Standalone Years in context: e.g. in 2024, since 1999, 1990s
    val CONTEXT_YEAR_PATTERN: Pattern = Pattern.compile(
        "\\b(?:in|since|by|during|from|until|year|around)\\s+(19\\d{2}|20\\d{2})s?\\b|\\b(19\\d{2}|20\\d{2})s\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Person with Title / Honorific / Role
    val PERSON_WITH_TITLE_PATTERN: Pattern = Pattern.compile(
        "\\b((?:Dr\\.|Prof\\.|Mr\\.|Ms\\.|Mrs\\.|CEO|CTO|CFO|COO|President|Director|Governor|Senator|Judge|Prime\\s+Minister|Minister|Sir|Lady|Lord|Principal\\s+Investigator|Chief\\s+Compliance\\s+Officer)\\s+[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)+)\\b"
    )

    // General Full Name (Capitalized Words: First Last or First Middle Last)
    val GENERAL_PERSON_NAME_PATTERN: Pattern = Pattern.compile(
        "\\b[A-Z][a-z]{1,20}\\s+(?:[A-Z]\\.\\s+)?[A-Z][a-z]{1,20}\\b"
    )

    // Organization Suffix Pattern (e.g. Acme Corp, OpenAI Inc, Harvard University, World Health Organization)
    val ORG_SUFFIX_PATTERN: Pattern = Pattern.compile(
        "\\b[A-Z][A-Za-z0-9&'. -]{1,40}\\s+(?:Inc\\.?|Corp\\.?|Corporation|Ltd\\.?|Limited|Co\\.?|Company|LLC|Group|Holdings|Technologies|Systems|Labs|Software|University|Institute|Foundation|Agency|Department|Ministry|Association|Commission|Federation|Bank|Hospital|College)\\b"
    )

    // Location Suffix / Direction Pattern (e.g. New York City, Washington State, South Korea, Western Europe)
    val LOCATION_SUFFIX_PATTERN: Pattern = Pattern.compile(
        "\\b(?:[A-Z][a-z]+\\s+(?:City|State|Country|County|Island|Islands|River|Mountain|Mount|Lake|Sea|Ocean|Valley))|(?:(?:North|South|East|West|Central|Northern|Southern|Eastern|Western)\\s+[A-Z][a-z]+)\\b"
    )

    // Words commonly mistaken for person names at sentence start or heading
    val NON_NAME_WORDS = setOf(
        "In", "On", "At", "The", "A", "An", "This", "That", "These", "Those", "Under", "After", "Before",
        "With", "Without", "From", "For", "To", "By", "About", "As", "If", "When", "While", "Where",
        "However", "Furthermore", "Moreover", "Therefore", "Consequently", "Thus", "First", "Second",
        "Third", "Finally", "Next", "Also", "Not", "Only", "Some", "Many", "All", "Each", "Every",
        "Another", "Other", "Both", "Either", "Neither", "New", "Old", "High", "Low", "Good", "Bad",
        "Great", "Small", "Large", "Major", "Minor", "General", "Special", "Public", "Private",
        "National", "International", "Global", "Local", "Source", "Document", "Table", "Figure",
        "Section", "Chapter", "Article", "Part", "Page", "Volume", "Issue", "Report", "Study",
        "Analysis", "Data", "Result", "Results", "Discussion", "Conclusion", "Method", "Methods"
    )

    // Known Organizations Dictionary
    val KNOWN_ORGANIZATIONS = listOf(
        "Alphabet", "Google", "DeepMind", "OpenAI", "Microsoft", "Apple", "Amazon", "Meta", "Tesla",
        "Nvidia", "Intel", "IBM", "Netflix", "Samsung", "Sony", "Oracle", "Cisco", "Adobe", "Salesforce",
        "Stanford University", "Harvard University", "MIT", "Oxford University", "Cambridge University",
        "Royal Society", "European Union", "United Nations", "WHO", "UNICEF", "UNESCO", "NASA", "CERN",
        "ClinicalTrials.gov", "NIST", "IEEE", "The Lancet", "Nature", "Science", "Cell", "FDA", "CDC",
        "FBI", "CIA", "SEC", "Federal Reserve", "BBC", "CNN", "Wall Street Journal", "New York Times"
    )

    // Known Locations Dictionary
    val KNOWN_LOCATIONS = listOf(
        "Mountain View", "California", "Geneva", "Tokyo", "New York", "London", "San Francisco", "Paris",
        "Berlin", "Boston", "Cambridge", "Zurich", "Washington", "Chicago", "Los Angeles", "Seattle",
        "Austin", "Toronto", "Sydney", "Beijing", "Shanghai", "Singapore", "Seoul", "Mumbai", "New Delhi",
        "Rome", "Madrid", "Amsterdam", "Vienna", "United States", "USA", "United Kingdom", "UK", "Canada",
        "Germany", "France", "Japan", "China", "India", "Australia", "Brazil", "Italy", "Spain", "Switzerland"
    )

    // Known Technology & Scientific Concepts Dictionary
    val KNOWN_TECHNOLOGIES = listOf(
        "transformer", "graph neural networks", "generative AI", "cloud infrastructure", "monoclonal antibody",
        "machine translation", "neural algorithmic reasoning", "numerical stability", "scaling laws",
        "symbolic systems", "connectionist models", "Python", "JavaScript", "TypeScript", "Java", "Kotlin",
        "Swift", "Rust", "C++", "Go", "SQL", "Docker", "Kubernetes", "Linux", "Android", "iOS", "Windows",
        "macOS", "AWS", "Azure", "GCP", "GraphQL", "REST API", "JSON", "HTML", "CSS", "React", "Vue",
        "Angular", "Node.js", "AI", "Machine Learning", "Deep Learning", "LLM", "GPT-4", "Gemini", "Claude",
        "Llama", "PyTorch", "TensorFlow", "OpenCV", "Blockchain", "5G", "Bluetooth", "Wi-Fi", "CRISPR", "RNA", "DNA"
    )
}

