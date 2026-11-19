// import { createContext, useContext, useEffect, useState } from "react";
// import en from "../i18n/en";
// import hi from "../i18n/hi";

// const LanguageContext = createContext(null);

// const translations = {
//   en,
//   hi,
// };

// export const LanguageProvider = ({ children }) => {
//   const [language, setLanguage] = useState(() => {
//     const savedLanguage = localStorage.getItem("tractorNowLanguage");

//     return savedLanguage === "hi" ? "hi" : "en";
//   });

//   useEffect(() => {
//     localStorage.setItem("tractorNowLanguage", language);
//   }, [language]);

//   const changeLanguage = (newLanguage) => {
//     if (newLanguage !== "en" && newLanguage !== "hi") {
//       return;
//     }

//     setLanguage(newLanguage);
//   };

//   /**
//    * Get translated text using:
//    * t("home.welcome")
//    * t("auth.login")
//    * t("equipment.tractor")
//    */
//   const t = (key) => {
//     const keys = key.split(".");

//     let value = translations[language];

//     for (const item of keys) {
//       value = value?.[item];
//     }

//     // Fallback to English if Hindi translation is missing
//     if (value === undefined) {
//       value = translations.en;

//       for (const item of keys) {
//         value = value?.[item];
//       }
//     }

//     // If translation does not exist
//     return value ?? key;
//   };

//   const value = {
//     language,
//     changeLanguage,
//     t,
//     isHindi: language === "hi",
//     isEnglish: language === "en",
//   };

//   return (
//     <LanguageContext.Provider value={value}>
//       {children}
//     </LanguageContext.Provider>
//   );
// };

// export const useLanguage = () => {
//   const context = useContext(LanguageContext);

//   if (!context) {
//     throw new Error(
//       "useLanguage must be used inside LanguageProvider"
//     );
//   }

//   return context;
// };

// export default LanguageContext;


import { createContext, useContext, useEffect, useState } from "react";

import en from "../i18n/en";
import hi from "../i18n/hi";

const LanguageContext = createContext(null);

const translations = {
  en,
  hi,
};

export const LanguageProvider = ({ children }) => {
  const [language, setLanguage] = useState(() => {
    const savedLanguage = localStorage.getItem(
      "tractorNowLanguage"
    );

    return savedLanguage === "hi" ? "hi" : "en";
  });

  useEffect(() => {
    localStorage.setItem(
      "tractorNowLanguage",
      language
    );
  }, [language]);

  const changeLanguage = (newLanguage) => {
    if (!["en", "hi"].includes(newLanguage)) {
      return;
    }

    setLanguage(newLanguage);
  };

  const t = (key) => {
    const keys = key.split(".");

    let result = translations[language];

    for (const keyPart of keys) {
      result = result?.[keyPart];
    }

    if (result !== undefined) {
      return result;
    }

    // English fallback
    result = translations.en;

    for (const keyPart of keys) {
      result = result?.[keyPart];
    }

    return result ?? key;
  };

  return (
    <LanguageContext.Provider
      value={{
        language,
        changeLanguage,
        t,
        isHindi: language === "hi",
        isEnglish: language === "en",
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);

  if (!context) {
    throw new Error(
      "useLanguage must be used inside LanguageProvider"
    );
  }

  return context;
};  