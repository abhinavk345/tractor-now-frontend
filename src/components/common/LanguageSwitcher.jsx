import { useLanguage } from "../../context/LanguageContext";

const LanguageSwitcher = () => {
  const { language, changeLanguage } = useLanguage();

  const isHindi = language === "hi";

  const toggleLanguage = () => {
    changeLanguage(isHindi ? "en" : "hi");
  };

  return (
    <button
      type="button"
      className="global-language-toggle"
      onClick={toggleLanguage}
      aria-label="Change language"
      title={isHindi ? "Switch to English" : "हिंदी में बदलें"}
    >
      <span className={!isHindi ? "language-active" : ""}>
        EN
      </span>

      <span className="language-toggle-track">
        <span
          className={`language-toggle-thumb ${
            isHindi ? "hindi" : ""
          }`}
        />
      </span>

      <span className={isHindi ? "language-active" : ""}>
        HI
      </span>
    </button>
  );
};

export default LanguageSwitcher;