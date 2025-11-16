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
