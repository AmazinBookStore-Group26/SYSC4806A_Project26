// user ID from localStorage or use default
const DEFAULT_USER_ID = localStorage.getItem("userId") || "default-user";

// Create book function
function createBook(formData) {
  fetch("/api/books", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(formData),
  })
    .then((response) => response.json())
    .then((data) => {
      alert("Book created successfully!");
      location.reload();
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to create book");
    });
}

// Update book function
function updateBook(bookId, formData) {
  fetch(`/api/books/${bookId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(formData),
  })
    .then((response) => response.json())
    .then((data) => {
      alert("Book updated successfully!");
      window.location.href = "/admin";
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to update book");
    });
}

// Delete book function
function deleteBook(bookId) {
  if (!confirm("Are you sure you want to delete this book?")) {
    return;
  }

  fetch(`/api/books/${bookId}`, {
    method: "DELETE",
  })
    .then(() => {
      alert("Book deleted successfully!");
      location.reload();
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to delete book");
    });
}

// Create book form
const createBookForm = document.getElementById("createBookForm");
if (createBookForm) {
  createBookForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = {
      isbn: document.getElementById("isbn").value,
      title: document.getElementById("title").value,
      description: document.getElementById("description").value,
      author: document.getElementById("author").value,
      publisher: document.getElementById("publisher").value,
      pictureUrl: document.getElementById("pictureUrl").value,
      price: parseFloat(document.getElementById("price").value),
      inventory: parseInt(document.getElementById("inventory").value),
      genre: document.getElementById("genre").value,
      publicationYear: parseInt(
        document.getElementById("publicationYear").value
      ),
    };
    createBook(formData);
  });
}

// Edit book form
const editBookForm = document.getElementById("editBookForm");
if (editBookForm) {
  editBookForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const bookId = this.dataset.bookId;
    const formData = {
      isbn: document.getElementById("isbn").value,
      title: document.getElementById("title").value,
      description: document.getElementById("description").value,
      author: document.getElementById("author").value,
      publisher: document.getElementById("publisher").value,
      pictureUrl: document.getElementById("pictureUrl").value,
      price: parseFloat(document.getElementById("price").value),
      inventory: parseInt(document.getElementById("inventory").value),
      genre: document.getElementById("genre").value,
      publicationYear: parseInt(
        document.getElementById("publicationYear").value
      ),
    };
    updateBook(bookId, formData);
  });
}

// Add to cart form
const addToCartForm = document.getElementById("addToCartForm");
if (addToCartForm) {
  addToCartForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const bookId = this.dataset.bookId;
    const quantity = document.getElementById("quantity").value;
    addToCart(bookId, quantity);
  });
}

// Add to cart function
function addToCart(bookId, quantity) {
  const url = `/api/cart/${DEFAULT_USER_ID}/items`;

  fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      bookId: bookId,
      quantity: parseInt(quantity),
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      alert("Book added to cart successfully!");
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to add book to cart");
    });
}

// Quick add to cart from browse page (adds 1 item)
function quickAddToCart(bookId) {
  const url = `/api/cart/${DEFAULT_USER_ID}/items`;

  fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      bookId: bookId,
      quantity: 1,
    }),
  })
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return response.json().then((err) => Promise.reject(err));
    })
    .then((data) => {
      // Show success feedback
      showNotification("Added to cart!", "success");
    })
    .catch((error) => {
      console.error("Error:", error);
      showNotification(error.message || "Failed to add to cart", "error");
    });
}

// Show notification toast
function showNotification(message, type) {
  // Remove existing notification if any
  const existing = document.querySelector(".notification-toast");
  if (existing) {
    existing.remove();
  }

  const toast = document.createElement("div");
  toast.className = `notification-toast ${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);

  // Trigger animation
  setTimeout(() => toast.classList.add("show"), 10);

  // Remove after 3 seconds
  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

// Remove from cart function
function removeFromCart(bookId) {
  if (!confirm("Are you sure you want to remove this item from cart?")) {
    return;
  }

  const url = `/api/cart/${DEFAULT_USER_ID}/items/${bookId}`;

  fetch(url, {
    method: "DELETE",
  })
    .then(() => {
      location.reload();
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to remove item from cart");
    });
}

// Update cart quantity
function updateCartQuantity(bookId, quantity) {
  const url = `/api/cart/${DEFAULT_USER_ID}/items/${bookId}?quantity=${quantity}`;

  fetch(url, {
    method: "PUT",
  })
    .then(() => {
      location.reload();
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to update quantity");
    });
}

/**
 * Show checkout modal
 */
function showCheckoutModal() {
  const modal = document.getElementById("checkoutModal");
  if (modal) {
    modal.style.display = "block";
  }
}

/**
 * Close checkout modal
 */
function closeCheckoutModal() {
  const modal = document.getElementById("checkoutModal");
  if (modal) {
    modal.style.display = "none";
    // Reset form
    document.getElementById("checkoutForm").reset();
  }
}

/**
 * Add formatting to card number input
 */
document.addEventListener("DOMContentLoaded", function () {
  const cardNumberInput = document.getElementById("cardNumber");
  if (cardNumberInput) {
    cardNumberInput.addEventListener("input", function (e) {
      let value = e.target.value.replace(/\s/g, "");
      let formattedValue = value.match(/.{1,4}/g)?.join(" ") || value;
      e.target.value = formattedValue;
    });
  }

  const expiryInput = document.getElementById("expiryDate");
  if (expiryInput) {
    expiryInput.addEventListener("input", function (e) {
      let value = e.target.value.replace(/\D/g, "");
      if (value.length >= 2) {
        value = value.slice(0, 2) + "/" + value.slice(2, 4);
      }
      e.target.value = value;
    });
  }

  // Close modal when clicking outside
  window.onclick = function (event) {
    const modal = document.getElementById("checkoutModal");
    if (event.target === modal) {
      closeCheckoutModal();
    }
  };
});

/**
 * Process checkout
 */
function processCheckout(event) {
  event.preventDefault();

  // Get form data for validation (simulated payment details)
  const cardName = document.getElementById("cardName").value;
  const cardNumber = document.getElementById("cardNumber").value;
  const expiryDate = document.getElementById("expiryDate").value;
  const cvv = document.getElementById("cvv").value;
  const billingAddress = document.getElementById("billingAddress").value;

  // Validate all fields are filled
  if (!cardName || !cardNumber || !expiryDate || !cvv || !billingAddress) {
    alert("Please fill in all payment details");
    return;
  }

  // Close modal
  closeCheckoutModal();

  // Call backend checkout API
  const url = `/api/orders/checkout/${DEFAULT_USER_ID}`;

  fetch(url, {
    method: "POST",
  })
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return response.json().then((err) => Promise.reject(err));
    })
    .then((data) => {
      alert(
        `Order placed successfully! Order ID: ${data.id}\n\nYour order has been confirmed and will be processed soon.`
      );
      window.location.href = "/orders?userId=" + DEFAULT_USER_ID;
    })
    .catch((error) => {
      console.error("Error:", error);
      alert(
        error.message ||
          "Failed to process checkout. Please check inventory availability."
      );
    });
}

// ======= Book Filtering and Sorting (Recommendations Page) =======

/**
 * Initialize recommendation page filters
 * Populates genre dropdown with unique genres from displayed books
 */
function initRecommendationFilters() {
  const genreSelect = document.getElementById("genreFilter");
  if (!genreSelect) return;

  const books = document.querySelectorAll(".book-card");
  const genres = new Set();

  books.forEach((book) => {
    const genre = book.dataset.genre;
    if (genre && genre.trim() !== "") {
      genres.add(genre);
    }
  });

  // add genres to dropdown
  Array.from(genres)
    .sort()
    .forEach((genre) => {
      const option = document.createElement("option");
      option.value = genre;
      option.textContent = genre;
      genreSelect.appendChild(option);
    });
}

/**
 * Filter books by genre
 */
function filterBooks() {
  const genreSelect = document.getElementById("genreFilter");
  if (!genreSelect) return;

  const genreFilter = genreSelect.value.toLowerCase();
  const books = document.querySelectorAll(".book-card");
  const bookGrid = document.getElementById("bookGrid");
  const noResults = document.getElementById("noResults");
  let visibleCount = 0;

  books.forEach((book) => {
    const genre = (book.dataset.genre || "").toLowerCase();
    const matchesGenre = !genreFilter || genre === genreFilter;

    if (matchesGenre) {
      book.style.display = "";
      visibleCount++;
    } else {
      book.style.display = "none";
    }
  });

  // show/hide no results message
  if (noResults) {
    noResults.style.display = visibleCount === 0 ? "block" : "none";
  }
  if (bookGrid) {
    bookGrid.style.display = visibleCount === 0 ? "none" : "grid";
  }
}

/**
 * Sort books by selected criteria
 */
function sortBooks() {
  const sortSelect = document.getElementById("sortBy");
  if (!sortSelect) return;

  const sortBy = sortSelect.value;
  const grid = document.getElementById("bookGrid");
  if (!grid) return;

  const books = Array.from(grid.querySelectorAll(".book-card"));

  books.sort((a, b) => {
    switch (sortBy) {
      case "title":
        return (a.dataset.title || "").localeCompare(b.dataset.title || "");
      case "title-desc":
        return (b.dataset.title || "").localeCompare(a.dataset.title || "");
      case "price":
        return parseFloat(a.dataset.price || 0) - parseFloat(b.dataset.price || 0);
      case "price-desc":
        return parseFloat(b.dataset.price || 0) - parseFloat(a.dataset.price || 0);
      case "author":
        return (a.dataset.author || "").localeCompare(b.dataset.author || "");
      default:
        return 0;
    }
  });

  // re-append in sorted order
  books.forEach((book) => grid.appendChild(book));
}

/**
 * Reset all filters and reload page
 */
function resetFilters() {
  const genreSelect = document.getElementById("genreFilter");
  const sortSelect = document.getElementById("sortBy");

  if (genreSelect) genreSelect.value = "";
  if (sortSelect) sortSelect.value = "default";

  // reload to get original order
  location.reload();
}

// Initialize filters when DOM is ready
document.addEventListener("DOMContentLoaded", function () {
  initRecommendationFilters();
});

// Quantity selector functions for book details page
function increaseQty() {
  const input = document.getElementById("quantity");
  if (input) {
    const max = parseInt(input.getAttribute("max")) || 999;
    const current = parseInt(input.value) || 1;
    if (current < max) {
      input.value = current + 1;
    }
  }
}

function decreaseQty() {
  const input = document.getElementById("quantity");
  if (input) {
    const current = parseInt(input.value) || 1;
    if (current > 1) {
      input.value = current - 1;
    }
  }
}
