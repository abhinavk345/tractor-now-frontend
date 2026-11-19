import React from "react";
import { useNavigate } from "react-router-dom";

import { useLanguage } from "../../context/LanguageContext";

const equipmentCategories = [
  {
    id: 1,
    name: "Tractor",
    translationKey: "equipment.tractor",
    icon: "🚜",
    count: 24,
  },
  {
    id: 2,
    name: "Rotavator",
    translationKey: "equipment.rotavator",
    icon: "🌾",
    count: 12,
  },
  {
    id: 3,
    name: "Trolley",
    translationKey: "equipment.trolley",
    icon: "🚚",
    count: 9,
  },
  {
    id: 4,
    name: "Sprayer",
    translationKey: "equipment.sprayer",
    icon: "💦",
    count: 7,
  },
  {
    id: 5,
    name: "Cultivator",
    translationKey: "equipment.cultivator",
    icon: "⚙️",
    count: 11,
  },
  {
    id: 6,
    name: "Harvester",
    translationKey: "equipment.harvester",
    icon: "🌾",
    count: 5,
  },
];

const nearbyEquipment = [
  {
    id: 1,
    name: "Mahindra 575 DI",
    type: "Tractor",
    translationKey: "equipment.tractor",
    icon: "🚜",
    village: "Rampur",
    district: "Patna",
    distance: 2.5,
    price: 900,
    rating: 4.7,
    reviews: 28,
    available: true,
    operatorAvailable: true,
    owner: "Rajesh Kumar",
  },
  {
    id: 2,
    name: "John Deere Rotavator",
    type: "Rotavator",
    translationKey: "equipment.rotavator",
    icon: "🌾",
    village: "Rampur",
    district: "Patna",
    distance: 4.2,
    price: 700,
    rating: 4.6,
    reviews: 18,
    available: true,
    operatorAvailable: true,
    owner: "Suresh Yadav",
  },
  {
    id: 3,
    name: "Farm Trolley",
    type: "Trolley",
    translationKey: "equipment.trolley",
    icon: "🚚",
    village: "Lakshmipur",
    district: "Patna",
    distance: 5.8,
    price: 600,
    rating: 4.5,
    reviews: 12,
    available: true,
    operatorAvailable: false,
    owner: "Manoj Kumar",
  },
  {
    id: 4,
    name: "Agricultural Sprayer",
    type: "Sprayer",
    translationKey: "equipment.sprayer",
    icon: "💦",
    village: "Bishunpur",
    district: "Patna",
    distance: 7.1,
    price: 450,
    rating: 4.4,
    reviews: 9,
    available: false,
    operatorAvailable: false,
    owner: "Amit Singh",
  },
];

const FarmerHome = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  return (
    <div className="app-layout">

      {/* ================= HEADER ================= */}

      <header className="top-header">

        <div className="header-brand">

          <div className="header-logo">
            🚜
          </div>

          <div>
            <h1>
              {t("app.name")}
            </h1>

            <span>
              {t("app.tagline")}
            </span>
          </div>

        </div>

        <div className="header-actions">

          <button
            type="button"
            className="icon-button"
            onClick={() =>
              navigate("/notifications")
            }
            aria-label={t("profile.notifications")}
            title={t("profile.notifications")}
          >
            🔔
          </button>

          <button
            type="button"
            className="profile-mini"
            onClick={() =>
              navigate("/farmer/profile")
            }
            aria-label={t("profile.profile")}
            title={t("profile.profile")}
          >
            👨‍🌾
          </button>

        </div>

      </header>

      {/* ================= MAIN ================= */}

      <main className="main-content">

        {/* ================= LOCATION ================= */}

        <section className="location-banner">

          <div className="location-icon">
            📍
          </div>

          <div className="location-info">

            <span>
              {t("location.currentLocation")}
            </span>

            <strong>
              Rampur, Patna
            </strong>

          </div>

          <button
            type="button"
            className="text-button"
            onClick={() =>
              navigate("/location")
            }
          >
            {t("location.changeLocation")}
          </button>

        </section>

        {/* ================= WELCOME ================= */}

        <section className="welcome-section">

          <h2>
            {t("home.findEquipment")}
          </h2>

          <p>
            {t("home.welcome")}
          </p>

        </section>

        {/* ================= SEARCH ================= */}

        <section className="search-section">

          <button
            type="button"
            className="search-box"
            onClick={() =>
              navigate("/equipment/search")
            }
          >

            <span className="search-icon">
              🔍
            </span>

            <span className="search-placeholder">
              {t("home.searchPlaceholder")}
            </span>

          </button>

        </section>

        {/* ================= CATEGORIES ================= */}

        <section className="section">

          <div className="section-header">

            <div>

              <h3>
                {t("home.categories")}
              </h3>

            </div>

            <button
              type="button"
              className="text-button"
              onClick={() =>
                navigate("/equipment/search")
              }
            >
              {t("common.view")}
            </button>

          </div>

          <div className="category-grid">

            {equipmentCategories.map(
              (category) => (

                <button
                  key={category.id}
                  type="button"
                  className="category-card"
                  onClick={() =>
                    navigate(
                      `/equipment/search?category=${category.name.toUpperCase()}`
                    )
                  }
                >

                  <span className="category-icon">
                    {category.icon}
                  </span>

                  <strong>
                    {t(
                      category.translationKey
                    )}
                  </strong>

                  <small>
                    {category.count}{" "}
                    {t("equipment.equipment")}
                  </small>

                </button>

              )
            )}

          </div>

        </section>

        {/* ================= NEARBY EQUIPMENT ================= */}

        <section className="section">

          <div className="section-header">

            <div>

              <h3>
                {t("home.nearbyEquipment")}
              </h3>

              <p>
                {t("location.nearby")}
              </p>

            </div>

            <button
              type="button"
              className="text-button"
              onClick={() =>
                navigate("/equipment/search")
              }
            >
              {t("common.view")}
            </button>

          </div>

          <div className="equipment-grid">

            {nearbyEquipment.map(
              (equipment) => (

                <article
                  key={equipment.id}
                  className="equipment-card"
                >

                  {/* Image */}

                  <div className="equipment-image">

                    <span>
                      {equipment.icon}
                    </span>

                    <div className="availability-badge">

                      <span
                        className={
                          equipment.available
                            ? "status-dot online"
                            : "status-dot offline"
                        }
                      />

                      {equipment.available
                        ? t(
                            "equipment.availableNow"
                          )
                        : t(
                            "equipment.currentlyBooked"
                          )}

                    </div>

                    <div className="distance-badge">

                      📍{" "}
                      {equipment.distance}{" "}
                      {t(
                        "location.kilometer"
                      )}

                    </div>

                  </div>

                  {/* Details */}

                  <div className="equipment-details">

                    <div className="equipment-type">

                      {t(
                        equipment.translationKey
                      )}

                    </div>

                    <h4>
                      {equipment.name}
                    </h4>

                    <div className="equipment-location">

                      📍{" "}
                      {equipment.village},{" "}
                      {equipment.district}

                    </div>

                    <div className="equipment-rating">

                      <span>
                        ⭐{" "}
                        {equipment.rating}
                      </span>

                      <span>
                        (
                        {equipment.reviews}{" "}
                        {t(
                          "equipment.reviews"
                        )}
                        )
                      </span>

                    </div>

                    <div className="equipment-owner">

                      {t(
                        "equipment.owner"
                      )}
                      :{" "}
                      {equipment.owner}

                    </div>

                    <div className="equipment-footer">

                      <div className="equipment-price">

                        <strong>
                          ₹{equipment.price}
                        </strong>

                        <span>
                          {t(
                            "equipment.pricePerHour"
                          )}
                        </span>

                      </div>

                      {equipment.operatorAvailable && (
                        <span className="operator-badge">

                          👨‍🔧{" "}
                          {t(
                            "equipment.operatorAvailable"
                          )}

                        </span>
                      )}

                    </div>

                    <button
                      type="button"
                      className="primary-button full-width"
                      disabled={
                        !equipment.available
                      }
                      onClick={() =>
                        navigate(
                          `/equipment/${equipment.id}`,
                          {
                            state: {
                              equipment,
                            },
                          }
                        )
                      }
                    >

                      {equipment.available
                        ? t("common.viewBook")
                        : t(
                            "equipment.currentlyBooked"
                          )}

                    </button>

                  </div>

                </article>

              )
            )}

          </div>

        </section>

        {/* ================= MAP ================= */}

        <section className="section">

          <div className="section-header">

            <div>

              <h3>
                {t("location.nearby")}
              </h3>

              <p>
                {t(
                  "equipment.availability"
                )}
              </p>

            </div>

            <button
              type="button"
              className="secondary-button"
              onClick={() =>
                navigate("/location")
              }
            >
              🗺️ {t("search.mapView")}
            </button>

          </div>

          <div className="map-preview">

            <div className="map-placeholder">

              <span>
                🗺️
              </span>

              <strong>
                {t("location.location")}
              </strong>

              <p>
                {t("location.nearby")}
              </p>

              <button
                type="button"
                className="primary-button"
                onClick={() =>
                  navigate("/location")
                }
              >
                {t(
                  "location.currentLocation"
                )}
              </button>

            </div>

          </div>

        </section>

      </main>

      {/* ================= BOTTOM NAVIGATION ================= */}

      <nav className="bottom-navigation">

        <button
          type="button"
          className="bottom-nav-item active"
          onClick={() =>
            navigate("/farmer/home")
          }
        >
          <span>🏠</span>

          <small>
            {t("navigation.home")}
          </small>
        </button>

        <button
          type="button"
          className="bottom-nav-item"
          onClick={() =>
            navigate("/equipment/search")
          }
        >
          <span>🔍</span>

          <small>
            {t("navigation.search")}
          </small>
        </button>

        <button
          type="button"
          className="bottom-nav-item"
          onClick={() =>
            navigate("/bookings")
          }
        >
          <span>📅</span>

          <small>
            {t("navigation.bookings")}
          </small>
        </button>

        <button
          type="button"
          className="bottom-nav-item"
          onClick={() =>
            navigate("/chat")
          }
        >
          <span>💬</span>

          <small>
            {t("navigation.chat")}
          </small>
        </button>

        <button
          type="button"
          className="bottom-nav-item"
          onClick={() =>
            navigate("/farmer/profile")
          }
        >
          <span>👤</span>

          <small>
            {t("navigation.profile")}
          </small>
        </button>

      </nav>

    </div>
  );
};

export default FarmerHome;