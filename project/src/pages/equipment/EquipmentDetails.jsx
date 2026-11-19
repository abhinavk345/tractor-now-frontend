import { useNavigate, useParams } from "react-router-dom";
import { useLanguage } from "../../context/LanguageContext";

const equipmentData = [
  {
    id: 1,
    name: "Mahindra 575 DI",
    translationKey: "equipment.tractor",
    owner: "Ramesh Kumar",
    village: "Rampur",
    district: "Patna",
    distance: 2.5,
    price: 900,
    rating: 4.7,
    reviews: 28,
    available: true,
    operatorAvailable: true,
    image:
      "https://images.unsplash.com/photo-1592982537447-6f2a6a0a7b9f?auto=format&fit=crop&w=1200&q=80",
    description:
      "Reliable tractor suitable for ploughing, cultivation, transportation and other agricultural activities.",
    specifications: {
      brand: "Mahindra",
      model: "575 DI",
      horsepower: "45 HP",
      fuel: "Diesel",
      transmission: "Manual",
      drive: "2WD",
    },
  },
  {
    id: 2,
    name: "John Deere Rotavator",
    translationKey: "equipment.rotavator",
    owner: "Suresh Yadav",
    village: "Rampur",
    district: "Patna",
    distance: 4.2,
    price: 700,
    rating: 4.6,
    reviews: 18,
    available: true,
    operatorAvailable: true,
    image:
      "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?auto=format&fit=crop&w=1200&q=80",
    description:
      "Agricultural rotavator suitable for soil preparation and field cultivation.",
    specifications: {
      brand: "John Deere",
      model: "Rotavator",
      width: "6 Feet",
      blades: "42",
      drive: "Tractor Mounted",
    },
  },
  {
    id: 3,
    name: "Farm Trolley",
    translationKey: "equipment.trolley",
    owner: "Mohan Singh",
    village: "Lakshmipur",
    district: "Patna",
    distance: 5.8,
    price: 600,
    rating: 4.5,
    reviews: 12,
    available: true,
    operatorAvailable: false,
    image:
      "https://images.unsplash.com/photo-1530267981375-f0de937f5f13?auto=format&fit=crop&w=1200&q=80",
    description:
      "Heavy-duty agricultural trolley for transportation of crops, seeds and farm materials.",
    specifications: {
      capacity: "5 Ton",
      type: "Agricultural Trolley",
      wheels: "4",
      usage: "Farm Transportation",
    },
  },
];

const EquipmentDetails = () => {
  const { equipmentId } = useParams();
  const navigate = useNavigate();
  const { t } = useLanguage();

  const equipment = equipmentData.find(
    (item) => item.id === Number(equipmentId)
  );

  if (!equipment) {
    return (
      <div className="equipment-details-page">
        <div className="equipment-details-empty">
          <div className="empty-icon">🚜</div>

          <h2>{t("home.noEquipment")}</h2>

          <button
            type="button"
            className="primary-button"
            onClick={() => navigate("/equipment/search")}
          >
            {t("common.back")}
          </button>
        </div>
      </div>
    );
  }

  const handleBooking = () => {
    navigate(`/booking/${equipment.id}`);
  };

  const handleChat = () => {
    navigate(`/chat/owner/${equipment.id}`);
  };

  return (
    <div className="equipment-details-page">
      <div className="equipment-details-container">
        {/* Back */}
        <button
          type="button"
          className="back-button"
          onClick={() => navigate(-1)}
        >
          ← {t("common.back")}
        </button>

        <div className="equipment-details-card">
          {/* Image */}
          <div className="equipment-details-image-section">
            <img
              src={equipment.image}
              alt={equipment.name}
              className="equipment-details-image"
            />

            <div
              className={`equipment-availability-badge ${
                equipment.available
                  ? "available"
                  : "unavailable"
              }`}
            >
              {equipment.available
                ? t("equipment.availableNow")
                : t("equipment.currentlyBooked")}
            </div>
          </div>

          {/* Information */}
          <div className="equipment-details-content">
            <div className="equipment-details-header">
              <div>
                <span className="equipment-type-label">
                  {t(equipment.translationKey)}
                </span>

                <h1>{equipment.name}</h1>
              </div>

              <div className="equipment-rating">
                ⭐ {equipment.rating}
                <span>
                  ({equipment.reviews} {t("equipment.reviews")})
                </span>
              </div>
            </div>

            {/* Owner */}
            <div className="owner-info-card">
              <div className="owner-avatar">
                {equipment.owner.charAt(0)}
              </div>

              <div className="owner-info">
                <span>{t("equipment.owner")}</span>
                <strong>{equipment.owner}</strong>
              </div>

              <button
                type="button"
                className="chat-owner-button"
                onClick={handleChat}
              >
                💬 {t("chat.startChat")}
              </button>
            </div>

            {/* Location */}
            <div className="equipment-location-card">
              <div className="detail-icon">📍</div>

              <div>
                <span>{t("location.location")}</span>

                <strong>
                  {equipment.village}, {equipment.district}
                </strong>

                <small>
                  {equipment.distance}{" "}
                  {t("location.kilometer")}{" "}
                  {t("location.nearby")}
                </small>
              </div>
            </div>

            {/* Price */}
            <div className="equipment-price-section">
              <span>{t("equipment.price")}</span>

              <strong>
                ₹{equipment.price}
                <small>{t("equipment.pricePerHour")}</small>
              </strong>
            </div>

            {/* Operator */}
            <div className="equipment-feature-grid">
              <div className="equipment-feature">
                <span>👨‍🌾</span>

                <div>
                  <small>
                    {t("equipment.operator")}
                  </small>

                  <strong>
                    {equipment.operatorAvailable
                      ? t("equipment.operatorAvailable")
                      : t("equipment.operatorRequired")}
                  </strong>
                </div>
              </div>

              <div className="equipment-feature">
                <span>📍</span>

                <div>
                  <small>
                    {t("location.distance")}
                  </small>

                  <strong>
                    {equipment.distance}{" "}
                    {t("location.kilometer")}
                  </strong>
                </div>
              </div>
            </div>

            {/* Description */}
            <section className="equipment-section">
              <h2>
                {t("equipment.description")}
              </h2>

              <p>{equipment.description}</p>
            </section>

            {/* Specifications */}
            <section className="equipment-section">
              <h2>
                {t("equipment.specifications")}
              </h2>

              <div className="specifications-grid">
                {Object.entries(
                  equipment.specifications
                ).map(([key, value]) => (
                  <div
                    className="specification-item"
                    key={key}
                  >
                    <span>
                      {key
                        .replace(/([A-Z])/g, " $1")
                        .replace(/^./, (char) =>
                          char.toUpperCase()
                        )}
                    </span>

                    <strong>{value}</strong>
                  </div>
                ))}
              </div>
            </section>

            {/* Actions */}
            <div className="equipment-details-actions">
              <button
                type="button"
                className="secondary-action-button"
                onClick={handleChat}
              >
                💬 {t("chat.startChat")}
              </button>

              <button
                type="button"
                className="primary-action-button"
                onClick={handleBooking}
                disabled={!equipment.available}
              >
                🚜 {t("common.viewBook")}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EquipmentDetails;