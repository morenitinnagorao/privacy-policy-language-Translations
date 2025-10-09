// ✅ Attach the event listener once, outside the function
document.getElementById('lang-selector').addEventListener('change', (event) => {
  setLanguage(event.target.value);
});

async function setLanguage(lang) {
  try {
    const elements = document.querySelectorAll('[data-key]');
    const translations = {};

    for (const element of elements) {
      const key = element.getAttribute('data-key');
      const originalText = element.textContent;

      const response = await fetch('http://localhost:8080/translate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: originalText, targetLang: lang })
      });

      const result = await response.json();
      translations[key] = result.translatedText;
      element.textContent = result.translatedText;
    }

    localStorage.setItem('lang', lang);
  } catch (error) {
    console.error('Translation error:', error);
  }
}
