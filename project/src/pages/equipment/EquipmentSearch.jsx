import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useLanguage } from "../../context/LanguageContext";
import "src/pages/equipment/EquipmentSearch.css";

const equipmentData = [
  {
    id: 1,
    name: "Mahindra 575 DI",
    type: "Tractor",
    translationKey: "equipment.tractor",
    category: "TRACTOR",
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
    category: "ROTAVATOR",
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
    category: "TROLLEY",
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
    category: "SPRAYER",
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
  {
    id: 5,
    name: "Swaraj Cultivator",
    type: "Cultivator",
    translationKey: "equipment.cultivator",
    category: "CULTIVATOR",
    icon: "⚙️",
    village: "Danapur",
    district: "Patna",
    distance: 8.4,
    price: 650,
    rating: 4.3,
    reviews: 14,
    available: true,
    operatorAvailable: true,
    owner: "Vijay Kumar",
  },
  {
    id: 6,
    name: "New Holland Harvester",
    type: "Harvester",
    translationKey: "equipment.harvester",
    category: "HARVESTER",
    icon: "🌾",
    village: "Bihta",
    district: "Patna",
    distance: 11.2,
    price: 1800,
    rating: 4.8,
    reviews: 32,
    available: true,
    operatorAvailable: true,
    owner: "Ramesh Singh",
  },
];

const EquipmentSearch = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("ALL");
  const [availability, setAvailability] = useState("ALL");
  const [operator, setOperator] = useState("ALL");
  const [maxDistance, setMaxDistance] = useState(50);
  const [maxPrice, setMaxPrice] = useState(5000);
  const [sortBy, setSortBy] = useState("NEAREST");

  const filteredEquipment = useMemo(() => {
    let result = equipmentData.filter((equipment) => {
      const searchValue = search.toLowerCase().trim();

      const matchesSearch =
        !searchValue ||
        equipment.name.toLowerCase().includes(searchValue) ||
        equipment.type.toLowerCase().includes(searchValue) ||
        equipment.village.toLowerCase().includes(searchValue);

      const matchesCategory =
        category === "ALL" ||
        equipment.category === category;

      const matchesAvailability =
        availability === "ALL" ||
        (availability === "AVAILABLE" &&
          equipment.available);

      const matchesOperator =
        operator === "ALL" ||
        (operator === "AVAILABLE" &&
          equipment.operatorAvailable);

      const matchesDistance =
        equipment.distance <= Number(maxDistance);

      const matchesPrice =
        equipment.price <= Number(maxPrice);

      return (
        matchesSearch &&
        matchesCategory &&
        matchesAvailability &&
        matchesOperator &&
        matchesDistance &&
        matchesPrice
      );
    });

    if (sortBy === "NEAREST") {
      result.sort(
        (a, b) => a.distance - b.distance
      );
    }

    if (sortBy === "LOWEST_PRICE") {
      result.sort(
        (a, b) => a.price - b.price
      );
    }

    if (sortBy === "HIGHEST_PRICE") {
      result.sort(
        (a, b) => b.price - a.price
      );
    }

    if (sortBy === "HIGHEST_RATED") {
      result.sort(
        (a, b) => b.rating - a.rating
      );
    }

    return result;
  }, [
    search,
    category,
    availability,
    operator,
    maxDistance,
    maxPrice,
    sortBy,
  ]);

  const clearFilters = () => {
    setSearch("");
    setCategory("ALL");
    setAvailability("ALL");
    setOperator("ALL");
    setMaxDistance(50);
    setMaxPrice(5000);
    setSortBy("NEAREST");
  };

  const handleEquipmentClick = (equipment) => {
    navigate(`/equipment/${equipment.id}`, {
      state: {
        equipment,
      },
    });
  };

  return (
    <div className="app-layout">

      {/* ================= HEADER ================= */}

      <header className="top-header">

        <div className="header-brand">

          <button
            type="button"
            className="icon-button"
            onClick={() =>
              navigate("/farmer/home")
            }
            aria-label={t("common.back")}
            title={t("common.back")}
          >
            ←
          </button>

          <div className="header-logo">
            🚜
          </div>

          <div>
            <h1>
              {t("app.name")}
            </h1>

            <span>
              {t("search.searchEquipment")}
            </span>
          </div>

        </div>

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

      </header>

      {/* ================= MAIN ================= */}

      <main className="main-content">

        {/* ================= PAGE HEADING ================= */}

        <section className="welcome-section">

          <h2>
            {t("search.searchEquipment")}
          </h2>

          <p>
            {t("home.nearbyEquipment")}
          </p>

        </section>

        {/* ================= SEARCH ================= */}

        <section className="search-section">

          <div className="search-box">

            <span
              className="search-icon"
              aria-hidden="true"
            >
              🔍
            </span>

            <input
              type="text"
              value={search}
              onChange={(e) =>
                setSearch(e.target.value)
              }
              placeholder={t(
                "home.searchPlaceholder"
              )}
              aria-label={t(
                "search.searchEquipment"
              )}
            />

            {search && (
              <button
                type="button"
                className="search-clear"
                onClick={() =>
                  setSearch("")
                }
                aria-label={t("common.close")}
                title={t("common.close")}
              >
                ×
              </button>
            )}

          </div>

        </section>

        {/* ================= FILTERS ================= */}

        <section className="section">

          <div className="section-header">

            <div>
              <h3>
                {t("search.filters")}
              </h3>
            </div>

            <button
              type="button"
              className="text-button"
              onClick={clearFilters}
            >
              {t("search.clearFilters")}
            </button>

          </div>

          <div className="filter-panel">

            {/* Category */}

            <div className="form-group">

              <label>
                {t("search.category")}
              </label>

              <select
                value={category}
                onChange={(e) =>
                  setCategory(e.target.value)
                }
              >

                <option value="ALL">
                  {t("search.allCategories")}
                </option>

                <option value="TRACTOR">
                  {t("equipment.tractor")}
                </option>

                <option value="ROTAVATOR">
                  {t("equipment.rotavator")}
                </option>

                <option value="TROLLEY">
                  {t("equipment.trolley")}
                </option>

                <option value="SPRAYER">
                  {t("equipment.sprayer")}
                </option>

                <option value="CULTIVATOR">
                  {t("equipment.cultivator")}
                </option>

                <option value="HARVESTER">
                  {t("equipment.harvester")}
                </option>

              </select>

            </div>

            {/* Availability */}

            <div className="form-group">

              <label>
                {t("search.availability")}
              </label>

              <select
                value={availability}
                onChange={(e) =>
                  setAvailability(
                    e.target.value
                  )
                }
              >

                <option value="ALL">
                  {t("search.allAvailability")}
                </option>

                <option value="AVAILABLE">
                  {t(
                    "equipment.availableNow"
                  )}
                </option>

              </select>

            </div>

            {/* Operator */}

            <div className="form-group">

              <label>
                {t("search.operator")}
              </label>

              <select
                value={operator}
                onChange={(e) =>
                  setOperator(e.target.value)
                }
              >

                <option value="ALL">
                  {t("search.allOperators")}
                </option>

                <option value="AVAILABLE">
                  {t(
                    "equipment.operatorAvailable"
                  )}
                </option>

              </select>

            </div>

            {/* Distance */}

            <div className="form-group">

              <label>
                {t("search.radius")}:{" "}
                {maxDistance}{" "}
                {t("location.kilometer")}
              </label>

              <input
                type="range"
                min="1"
                max="50"
                value={maxDistance}
                onChange={(e) =>
                  setMaxDistance(
                    e.target.value
                  )
                }
              />

            </div>

            {/* Price */}

            <div className="form-group">

              <label>
                {t("search.maximumPrice")}: ₹
                {maxPrice}
              </label>

              <input
                type="range"
                min="100"
                max="5000"
                step="100"
                value={maxPrice}
                onChange={(e) =>
                  setMaxPrice(
                    e.target.value
                  )
                }
              />

            </div>

            {/* Sort */}

            <div className="form-group">

              <label>
                {t("search.sortBy")}
              </label>

              <select
                value={sortBy}
                onChange={(e) =>
                  setSortBy(e.target.value)
                }
              >

                <option value="NEAREST">
                  {t("search.nearest")}
                </option>

                <option value="LOWEST_PRICE">
                  {t("search.lowestPrice")}
                </option>

                <option value="HIGHEST_PRICE">
                  {t("search.highestPrice")}
                </option>

                <option value="HIGHEST_RATED">
                  {t("search.highestRated")}
                </option>

              </select>

            </div>

          </div>

        </section>

        {/* ================= RESULTS ================= */}

        <section className="section">

          <div className="section-header">

            <div>

              <h3>
                {filteredEquipment.length}{" "}
                {t("equipment.equipment")}
              </h3>

              <p>
                {t("location.nearby")}
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

          <div className="equipment-grid">

            {filteredEquipment.length === 0 ? (

              <div className="empty-state">

                <div>
                  🔍
                </div>

                <h3>
                  {t("search.noResults")}
                </h3>

                <p>
                  {t(
                    "search.tryDifferentSearch"
                  )}
                </p>

                <button
                  type="button"
                  className="primary-button"
                  onClick={clearFilters}
                >
                  {t("common.retry")}
                </button>

              </div>

            ) : (

              filteredEquipment.map(
                (equipment) => (

                  <article
                    key={equipment.id}
                    className="equipment-card"
                  >

                    {/* Equipment image */}

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

                    {/* Equipment details */}

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
                              "equipment.operator"
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
                          handleEquipmentClick(
                            equipment
                          )
                        }
                      >

                        {equipment.available
                          ? t(
                              "common.viewBook"
                            )
                          : t(
                              "equipment.currentlyBooked"
                            )}

                      </button>

                    </div>

                  </article>

                )
              )

            )}

          </div>

        </section>

      </main>

      {/* ================= BOTTOM NAVIGATION ================= */}

      <nav className="bottom-navigation">

        <button
          type="button"
          className="bottom-nav-item"
          onClick={() =>
            navigate("/farmer/home")
          }
          aria-label={t(
            "navigation.home"
          )}
        >
          <span>🏠</span>

          <small>
            {t("navigation.home")}
          </small>
        </button>

        <button
          type="button"
          className="bottom-nav-item active"
          aria-label={t(
            "navigation.search"
          )}
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
          aria-label={t(
            "navigation.bookings"
          )}
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
          aria-label={t(
            "navigation.chat"
          )}
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
          aria-label={t(
            "navigation.profile"
          )}
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

export default EquipmentSearch;