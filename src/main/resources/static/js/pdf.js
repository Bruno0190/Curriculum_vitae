const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox'] // '--no-sandbox' è obbligatorio sui server Linux come Render
  });

  const page = await browser.newPage();

  // 1. ORA PUNTA AL TUO SITO REALE SU RENDER (Sostituisci con il tuo vero URL)
  // Se vuoi scaricare un CV specifico, passerai anche l'ID, es: /curriculums/show/1
  await page.goto('https://il-tuo-app-name.onrender.com/', {
    waitUntil: 'networkidle0'
  });

  // 2. MODIFICA: Non salviamo su file ('path'), ma creiamo il PDF in memoria
  const pdfBuffer = await page.pdf({
    format: 'A4',
    printBackground: true, // Questo serve a includere i colori di sfondo e il CSS della navbar/grafica
    margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' } // Spesso utile per non attaccare il testo ai bordi del foglio
  });

  await browser.close();

  // Ora 'pdfBuffer' contiene il PDF sotto forma di byte. 
  // Se sei in un server Express.js, potrai inviarlo direttamente al browser dell'utente così:
  // res.contentType("application/pdf");
  // res.send(pdfBuffer);
})();








/* const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({
    headless: true,
    args: ['--no-sandbox']
  });

  const page = await browser.newPage();

  await page.goto('http://localhost:8080/', {
    waitUntil: 'networkidle0'
  });

  await page.pdf({
    path: 'cv.pdf',
    format: 'A4',
    printBackground: true
  });

  await browser.close();
})(); */